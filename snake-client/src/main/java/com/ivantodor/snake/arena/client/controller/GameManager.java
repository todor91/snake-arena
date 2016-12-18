package com.ivantodor.snake.arena.client.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.ivantodor.snake.arena.client.view.*;
import com.ivantodor.snake.arena.client.websocket.WebsocketClient;
import com.ivantodor.snake.arena.client.websocket.WebsocketClientException;
import com.ivantodor.snake.arena.common.model.MatchConstraints;
import com.ivantodor.snake.arena.common.request.MatchInvitationRequest;
import com.ivantodor.snake.arena.common.request.PlayerListRequest;
import com.ivantodor.snake.arena.common.request.SpectateRequest;
import com.ivantodor.snake.arena.common.response.*;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.MessageHandler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static com.ivantodor.snake.arena.client.helper.JsonMapper.mapper;

/**
 * @author Ivan Todorovic
 */
public class GameManager implements MessageHandler.Whole<String>
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    private GameView gameView;
    private MatchListView matchListView;
    private PlayerListView playerListView;
    private ConnectionView connectionView;
    private StatusView statusView;

    private String username;

    private WebsocketClient websocketClient;
    private static GameManager instance = new GameManager();

    private LinkedBlockingQueue<MatchStatusResponse> responsesQueue;
    private SnakeAIThread snakeAIThread;

    private String spectatingMatchId = "";
    private String lastInvitationId = null;
    public static GameManager getInstance()
    {
        return instance; // already initialized
    }

    private GameManager()
    {
        websocketClient = new WebsocketClient();
        websocketClient.setMessageHandler(this);
        responsesQueue = new LinkedBlockingQueue<>(512);

        snakeAIThread = new SnakeAIThread(responsesQueue, websocketClient);
        snakeAIThread.setDaemon(false);
        snakeAIThread.start();
    }

    public void connectToGameServer(String host, String name)
    {
        if(!websocketClient.isOpen())
        {
            username = name;
            try
            {
                String encodedName = URLEncoder.encode(name, "UTF-8");
                websocketClient.connect(host + encodedName);
            }
            catch (WebsocketClientException e)
            {
                logger.error("Connecting to game server failed", e);
                Platform.runLater(() -> connectionView.setConnected(false));
            }
            catch (UnsupportedEncodingException e)
            {
                logger.error("Unsupported encoding exception", e);
                Platform.runLater(() -> connectionView.setConnected(false));
            }
        }
    }

    public void shutdown()
    {
        snakeAIThread.shutdown();
        if(snakeAIThread.getState() == Thread.State.BLOCKED || snakeAIThread.getState() == Thread.State.WAITING)
            snakeAIThread.interrupt(); // If thread is blocked
        websocketClient.disconnect();
    }

    public void disconnectFromGameServer()
    {
        if(websocketClient.isOpen())
        {
            websocketClient.disconnect();
            Platform.runLater( () -> connectionView.setConnected(false));
            Platform.runLater( () -> matchListView.removeAll());
        }
    }

    public void sendInvitationResponse(MatchInvitationResponse response)
    {
        websocketClient.sendMessage(convertToJson(response));
    }

    public void refreshPlayerList()
    {
        websocketClient.sendMessage(convertToJson(new PlayerListRequest()));
    }

    public void invitePlayers(List<String> invitedPlayers, MatchConstraints matchConstraints)
    {
        MatchInvitationRequest request = new MatchInvitationRequest(invitedPlayers, matchConstraints);
        websocketClient.sendMessage(convertToJson(request));
        lastInvitationId = request.getInvitationId();
    }

    public void spectateMatch(String matchId)
    {
        if(spectatingMatchId.equals(matchId))
            return;

        this.spectatingMatchId = matchId;

        SpectateRequest request = new SpectateRequest(matchId);
        websocketClient.sendMessage(convertToJson(request));
    }

    @Override
    public void onMessage(String s)
    {
        JsonNode json = null;
        try
        {
            json = mapper.readTree(s);
        }
        catch (IOException e)
        {
            logger.error("Could not parse websocket message!", e);
        }

        if(json != null)
        {
            String type = json.get("type").textValue();

            switch(type)
            {
                case ServerRejectResponse.TYPE:
                    ServerRejectResponse response = parseJson(json, ServerRejectResponse.class);
                    if(response != null)
                    {
                        Platform.runLater(() -> connectionView.connectionRejected(response.getMessage()));
                        websocketClient.disconnect();
                    }
                    break;

                case MatchDiscoverResponse.TYPE:
                    MatchDiscoverResponse discoverResponse = parseJson(json, MatchDiscoverResponse.class);
                    if(discoverResponse != null)
                        Platform.runLater(() -> matchListView.addMatch(discoverResponse.getMatchId()));
                    break;

                case PlayerListResponse.TYPE:
                    PlayerListResponse playerListResponse = parseJson(json, PlayerListResponse.class);
                    if(playerListResponse != null)
                    {
                        List<String> otherPlayers = playerListResponse.getPlayers().stream().filter(e -> !e.equals(username)).collect(Collectors.toList());
                        Platform.runLater(() -> playerListView.setPlayers(otherPlayers));
                    }
                    break;

                case FinishedMatchResponse.TYPE:
                    FinishedMatchResponse finishedResponse = parseJson(json, FinishedMatchResponse.class);
                    if(finishedResponse != null)
                        Platform.runLater(() -> matchListView.removeMatch(finishedResponse.getMatchId()));

                    break;

                case MatchStatusResponse.TYPE:
                    MatchStatusResponse matchStatusResponse = parseJson(json, MatchStatusResponse.class);
                    if(matchStatusResponse != null)
                    {
                        // If we sent the request, we want to switch to spectate that game when response arrives
                        if(lastInvitationId != null && matchStatusResponse.getMatchId().equals(lastInvitationId))
                        {
                            spectateMatch(lastInvitationId);
                            lastInvitationId = null;
                        }

                        if(spectatingMatchId.equals(matchStatusResponse.getMatchId()))
                        {
                            Platform.runLater(() -> gameView.processMatchStatus(matchStatusResponse));
                            Platform.runLater(() -> statusView.updateStatusView(matchStatusResponse.getMatchId(), matchStatusResponse.getMatchState(), matchStatusResponse.getScores()));
                        }

                        //Adding status to the AI thread
                        try
                        {
                            // If user's snake is on the board, only then request action from the AI
                            if(matchStatusResponse.getSnakes().containsKey(username))
                                responsesQueue.put(matchStatusResponse);

                        }
                        catch (InterruptedException e)
                        {
                            logger.error("Could not add MatchStatusResponse to the queue", e);
                        }

                    }
                    break;

                case MatchInvitationRequest.TYPE:
                    MatchInvitationRequest request = parseJson(json, MatchInvitationRequest.class);
                    Platform.runLater(() -> playerListView.processInvitation(request));
                    break;
            }
        }
    }

    public void setGameView(GameView gameView)
    {
        this.gameView = gameView;
    }

    public void setMatchListView(MatchListView matchListView)
    {
        this.matchListView = matchListView;
    }

    public void setPlayerListView(PlayerListView playerListView)
    {
        this.playerListView = playerListView;
    }

    public void setConnectionView(ConnectionView connectionView)
    {
        this.connectionView = connectionView;
    }

    public void setStatusView(StatusView statusView)
    {
        this.statusView = statusView;
    }

    public String getUsername()
    {
        return username;
    }

    public void setAI(SnakeAI snakeAI)
    {
        this.snakeAIThread.setSnakeAI(snakeAI);
    }

    private <T> String convertToJson(T object)
    {
        try
        {
            return mapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e)
        {
            logger.error("Could not convert object to JSON", e);
            return null;
        }
    }

    private <T> T parseJson(JsonNode jsonNode, Class<T> type)
    {
        try
        {
            return mapper.treeToValue(jsonNode, type);
        }
        catch (JsonProcessingException e)
        {
            logger.error("Error while json parsing", e);
            return null;
        }
    }
}
