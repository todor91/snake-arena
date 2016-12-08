package com.ivantodor.snake.arena.client.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.ivantodor.snake.arena.client.model.Board;
import com.ivantodor.snake.arena.client.model.Snake;
import com.ivantodor.snake.arena.client.view.*;
import com.ivantodor.snake.arena.client.websocket.WebsocketClient;
import com.ivantodor.snake.arena.client.websocket.WebsocketClientException;
import com.ivantodor.snake.arena.common.MoveAction;
import com.ivantodor.snake.arena.common.model.Direction;
import com.ivantodor.snake.arena.common.request.MatchDiscoverRequest;
import com.ivantodor.snake.arena.common.request.MatchInvitationRequest;
import com.ivantodor.snake.arena.common.request.PlayerListRequest;
import com.ivantodor.snake.arena.common.response.*;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.MessageHandler;
import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private SnakeAI snakeAI;

    private WebsocketClient websocketClient;
    private static GameManager instance = new GameManager();


    public static GameManager getInstance()
    {
        return instance; // already initialized
    }

    private GameManager()
    {
        websocketClient = new WebsocketClient();
        websocketClient.setMessageHandler(this);
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

    public void disconnectFromGameServer()
    {
        if(websocketClient.isOpen())
        {
            websocketClient.disconnect();
            Platform.runLater( () -> connectionView.setConnected(false));
        }
    }

    public void refreshMatchList()
    {
        String discoverRequestJson = convertToJson(new MatchDiscoverRequest());
        websocketClient.sendMessage(discoverRequestJson);
    }

    public void sendInvitationResponse(MatchInvitationResponse response)
    {
        websocketClient.sendMessage(convertToJson(response));
    }

    public void refreshPlayerList()
    {
        websocketClient.sendMessage(convertToJson(new PlayerListRequest()));
    }

    public void invitePlayers(List<String> invitedPlayers, int boardSize)
    {
        MatchInvitationRequest request = new MatchInvitationRequest(boardSize, invitedPlayers);
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
            logger.debug("Processing type = " + type);

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

                case MatchStatusResponse.TYPE:
                    MatchStatusResponse matchStatusResponse = parseJson(json, MatchStatusResponse.class);
                    if(matchStatusResponse != null)
                    {
                        Platform.runLater(() -> gameView.processMatchStatus(matchStatusResponse));
                        Platform.runLater(() -> statusView.updateStatusView(matchStatusResponse.getMatchId(), matchStatusResponse.getMatchState(), matchStatusResponse.getScores()));

                        List<Point> snakePoints = matchStatusResponse.getSnakes().get(username);
                        Snake playerSnake = new Snake(snakePoints);
                        List<Snake> enemySnakes = new ArrayList<>();

                        for (Map.Entry<String, List<Point>> entry : matchStatusResponse.getSnakes().entrySet())
                            if(!entry.getKey().equals(username))
                                enemySnakes.add(new Snake(entry.getValue()));

                        Board board = new Board(matchStatusResponse.getSize(), playerSnake, enemySnakes, matchStatusResponse.getFoodPosition());

                        Direction action = snakeAI.move(board);


                        MoveAction request = new MoveAction(matchStatusResponse.getMatchId(), action);
                        try
                        {
                            String message = mapper.writeValueAsString(request);
                            websocketClient.sendMessage(message);
                        }
                        catch (JsonProcessingException e)
                        {
                            logger.error("Could not send move Action");
                            e.printStackTrace();
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

    public void setAI(SnakeAI snakeAI)
    {
        this.snakeAI = snakeAI;
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
