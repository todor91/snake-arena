package com.ivantodor.snake.arena.client.view.impl;

import com.ivantodor.snake.arena.client.view.StatusView;
import com.ivantodor.snake.arena.client.view.helper.ColorGenerator;
import com.ivantodor.snake.arena.client.view.helper.PoolColorGenerator;
import com.ivantodor.snake.arena.common.model.MatchState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Map;

/**
 * @author Ivan Todorovic
 */
public class StatusPane extends BorderPane implements StatusView
{
    private ColorGenerator colorGenerator = new PoolColorGenerator();

    private FlowPane scoresPane = new FlowPane();
    private Label matchIdLabel = new Label("----");
    private Label matchStateLabel = new Label("State: /");
    public StatusPane()
    {
        setupUI();
    }

    private void setupUI()
    {
        this.setPadding(new Insets(10, 10, 10, 10));
        this.setStyle("-fx-border-color: black");

        scoresPane.setPadding(new Insets(10, 0, 10, 0));
        scoresPane.setVgap(10);
        scoresPane.setHgap(10);
        scoresPane.setPrefHeight(50);
        scoresPane.setAlignment(Pos.BASELINE_CENTER);

        VBox topPane = new VBox();
        topPane.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Match Status");
        topPane.getChildren().addAll(titleLabel, matchIdLabel, matchStateLabel);

        this.setTop(topPane);
        this.setCenter(scoresPane);
    }

    @Override
    public void updateStatusView(String matchId, MatchState matchState, Map<String, Integer> scores)
    {
        matchIdLabel.setText("--" + matchId + "--");
        if(matchState == MatchState.DONE)
            matchStateLabel.setText("State: WINNER = " + maxScore(scores));
        else
            matchStateLabel.setText("State: " + matchState.toString());

        scoresPane.getChildren().clear();
        for (Map.Entry<String, Integer> playerScore: scores.entrySet())
        {
            Label playerLabel = new Label(playerScore.getKey() + "(" + playerScore.getValue() + ")");
            playerLabel.setStyle("-fx-border-color: black");
            playerLabel.setFont(Font.font("Cambria", 16));

            Color c = colorGenerator.getColor(playerScore.getKey());

            playerLabel.setBackground(new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY)));
            playerLabel.setTextFill(c.invert());

            scoresPane.getChildren().add(playerLabel);
        }
    }

    private String maxScore(Map<String, Integer> scoreMap)
    {
        String maxName = "";
        Integer maxScore = Integer.MIN_VALUE;

        for (Map.Entry<String, Integer> entry : scoreMap.entrySet())
        if(entry.getValue() > maxScore)
        {
            maxName = entry.getKey();
            maxScore = entry.getValue();
        }
        return maxName;
    }
}
