package com.ivantodor.snake.arena.client.view.impl;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 * @author Ivan Todorovic
 */
public class MainWindowPane extends BorderPane
{
    private Pane connectionPane;
    private Pane playerListPane;
    private Pane matchListPane;
    private Pane gamePane;
    private Pane statusPane;

    public MainWindowPane(Pane connectionPane, Pane playerListPane, MatchListPane matchListPane, GamePane gamePane, StatusPane statusPane)
    {
        this.connectionPane = connectionPane;
        this.playerListPane = playerListPane;
        this.matchListPane = matchListPane;
        this.gamePane = gamePane;
        this.statusPane = statusPane;
        initUI();
    }

    private void initUI()
    {
        this.setTop(connectionPane);
        this.setLeft(playerListPane);
        this.setRight(matchListPane);
        this.setCenter(gamePane);
        this.setBottom(statusPane);
    }
}