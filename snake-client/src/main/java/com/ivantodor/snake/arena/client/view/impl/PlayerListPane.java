package com.ivantodor.snake.arena.client.view.impl;

import com.ivantodor.snake.arena.client.controller.GameManager;
import com.ivantodor.snake.arena.client.view.PlayerListView;
import com.ivantodor.snake.arena.client.view.helper.InvitationDialog;
import com.ivantodor.snake.arena.common.model.MatchConstraints;
import com.ivantodor.snake.arena.common.request.MatchInvitationRequest;
import com.ivantodor.snake.arena.common.response.MatchInvitationResponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author Ivan Todorovic
 */
public class PlayerListPane extends VBox implements PlayerListView
{
    private Button refreshButton = new Button("Refresh");
    private Button inviteButton = new Button("Invite");

    private ListView<String> list = new ListView<>();

    public PlayerListPane()
    {
        setupUI();
    }

    private void setupUI()
    {
        HBox buttonPane = new HBox(5);
        buttonPane.getChildren().addAll(refreshButton, inviteButton);
        buttonPane.setAlignment(Pos.CENTER);
        buttonPane.setPrefHeight(50);
        this.getChildren().addAll(list, buttonPane);
        this.setPrefWidth(180);
        list.prefHeightProperty().bind(heightProperty().subtract(50));
        list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        refreshButton.setOnAction(e -> refreshPlayerList());
        inviteButton.setOnAction(e -> invitePlayers());
    }

    public void setPlayers(List<String> playerNames)
    {
        ObservableList<String> items = FXCollections.observableArrayList(playerNames);
        list.setItems(items);
    }

    @Override
    public void processInvitation(MatchInvitationRequest request)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Invitation");
        alert.setHeaderText("Game invitation");
        alert.setContentText("List of players:");

        StringBuilder totalString = new StringBuilder("");
        for (String name : request.getInvitedPlayers())
            totalString.append(name + ", ");

        Optional<ButtonType> result = alert.showAndWait();
        MatchInvitationResponse response;

        if (result.get() == ButtonType.OK)
            response = new MatchInvitationResponse(request.getInvitationId(), true);
        else
            response = new MatchInvitationResponse(request.getInvitationId(), false);

        GameManager.getInstance().sendInvitationResponse(response);
    }

    private void refreshPlayerList()
    {
        list.getItems().clear();
        GameManager.getInstance().refreshPlayerList();
    }

    private void invitePlayers()
    {
        List<String> invited = new LinkedList<>();
        invited.addAll(list.getSelectionModel().getSelectedItems());

        Dialog<MatchConstraints> dialog = InvitationDialog.create(invited);

        Optional<MatchConstraints> result = dialog.showAndWait();
        if (result.isPresent())
            GameManager.getInstance().invitePlayers(invited, result.get());
    }
}