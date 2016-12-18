package com.ivantodor.snake.arena.client.view.impl;

import com.ivantodor.snake.arena.client.controller.GameManager;
import com.ivantodor.snake.arena.client.view.MatchListView;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * @author Ivan Todorovic
 */
public class MatchListPane extends VBox implements MatchListView
{
    private Button spectateButton = new Button("Spectate");

    private ListView<String> list = new ListView<>();

    public MatchListPane()
    {
        setupUI();
    }

    private void setupUI()
    {
        HBox buttonPane = new HBox(5);
        buttonPane.getChildren().addAll(spectateButton);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setPrefHeight(50);

        this.getChildren().addAll(list, buttonPane);
        this.setPrefWidth(180);

        list.prefHeightProperty().bind(heightProperty().subtract(50));

        spectateButton.setOnAction(action -> spectateMatch());
    }

    public void addMatch(String match)
    {
        ObservableList<String> items = list.getItems();
        items.add(match);
        list.setItems(items);
    }

    public void removeAll()
    {
        list.getItems().clear();
    }

    public void removeMatch(String matchId)
    {
        ObservableList<String> items = list.getItems();
        items.remove(matchId);
        list.setItems(items);
    }

    private void spectateMatch()
    {
        String matchId = list.getSelectionModel().getSelectedItem();
        if(matchId != null)
        {
            GameManager.getInstance().spectateMatch(matchId);
        }
    }
}