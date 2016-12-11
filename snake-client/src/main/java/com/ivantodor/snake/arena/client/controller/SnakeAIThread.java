package com.ivantodor.snake.arena.client.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ivantodor.snake.arena.client.model.Board;
import com.ivantodor.snake.arena.client.model.Snake;
import com.ivantodor.snake.arena.client.websocket.WebsocketClient;
import com.ivantodor.snake.arena.common.MoveAction;
import com.ivantodor.snake.arena.common.model.Direction;
import com.ivantodor.snake.arena.common.response.MatchStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import static com.ivantodor.snake.arena.client.helper.JsonMapper.mapper;

/**
 * @author Ivan Todorovic
 */
public class SnakeAIThread extends Thread
{
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    private LinkedBlockingQueue<MatchStatusResponse> statusResponseQueue;
    private WebsocketClient client;
    private SnakeAI snakeAI;
    private boolean active = true;

    public SnakeAIThread(LinkedBlockingQueue<MatchStatusResponse> statusResponseQueue, WebsocketClient client)
    {
        this.statusResponseQueue = statusResponseQueue;
        this.client = client;
    }

    public void shutdown()
    {
        active = false;
    }

    @Override
    public void run()
    {
        while(active)
        {
            try
            {
                MatchStatusResponse matchStatusResponse = statusResponseQueue.take();

                if(snakeAI == null)
                {
                    logger.error("AI not set. No move actions will be sent");
                    continue;
                }

                Board board = constructBoardObject(matchStatusResponse);

                Direction moveActionResponse = snakeAI.move(board);

                MoveAction request = new MoveAction(matchStatusResponse.getMatchId(), moveActionResponse);

                try
                {
                    String message = mapper.writeValueAsString(request);
                    client.sendMessage(message);
                }
                catch (JsonProcessingException e)
                {
                    logger.error("Could not send MoveAction", e);
                }
            }
            catch (InterruptedException e)
            {
                logger.warn("AI thread has been interrupted");
            }
        }
    }

    public void setSnakeAI(SnakeAI snakeAI)
    {
        this.snakeAI = snakeAI;
    }

    private Board constructBoardObject(MatchStatusResponse matchStatusResponse)
    {
        String username = GameManager.getInstance().getUsername();
        List<Point> snakePoints = matchStatusResponse.getSnakes().get(username);
        Snake playerSnake = new Snake(snakePoints);
        List<Snake> enemySnakes = new ArrayList<>();

        for (Map.Entry<String, java.util.List<Point>> entry : matchStatusResponse.getSnakes().entrySet())
            if(!entry.getKey().equals(username))
                enemySnakes.add(new Snake(entry.getValue()));

        return new Board(matchStatusResponse.getSize(), playerSnake, enemySnakes, matchStatusResponse.getFoodPosition());
    }
}
