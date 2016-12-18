package com.ivantodor.snake.arena.client;

import com.ivantodor.snake.arena.client.controller.GameManager;
import com.ivantodor.snake.arena.client.controller.SnakeAI;
import com.ivantodor.snake.arena.client.model.Board;
import com.ivantodor.snake.arena.client.view.impl.*;
import com.ivantodor.snake.arena.common.model.Direction;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * @author Ivan Todorovic
 */
public class Main extends Application
{
    private Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args)
    {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception
    {
        ConnectionPane connectionView = new ConnectionPane();
        PlayerListPane playerListView = new PlayerListPane();
        MatchListPane matchListView = new MatchListPane();
        GamePane gameView = new GamePane();
        StatusPane statusView = new StatusPane();

        MainWindowPane win = new MainWindowPane(connectionView, playerListView, matchListView, gameView, statusView);

        // Game facade
        GameManager gameManager = GameManager.getInstance();

        // Set all GUI views
        gameManager.setConnectionView(connectionView);
        gameManager.setGameView(gameView);
        gameManager.setMatchListView(matchListView);
        gameManager.setPlayerListView(playerListView);
        gameManager.setStatusView(statusView);

        // Set AI
        gameManager.setAI(new SnakeAI() {
            Direction[] dirArray = new Direction[] { Direction.FORWARD, Direction.LEFT, Direction.RIGHT };
            @Override
            public Direction move(Board board)
            {
                Random random = new Random();
                return dirArray[random.nextInt(dirArray.length)];
            }
        });


        // Initialize final window
        Scene scene = new Scene(win, 760, 510);

        // Proper application shutdown
        primaryStage.setOnCloseRequest(event ->
        {
            gameManager.shutdown();
            try
            {
                stop();
            }
            catch (Exception e)
            {
                logger.error("Exception during application shutdown", e);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}