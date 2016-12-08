package com.ivantodor.snake.arena.client.view.helper;

import com.ivantodor.snake.arena.common.model.MatchConstraints;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * @author Ivan Todorovic
 */

public class InvitationDialog extends Dialog<MatchConstraints>
{
    private static final String ICON_PATH = "/images/snake-icon.png";

    private InvitationDialog(List<String> invitedPlayers)
    {
        this.setTitle("Invitation Dialog");
        this.setHeaderText("Configure match constraints");

        setupUI(invitedPlayers);
    }

    private void setupUI(List<String> invitedPlayers)
    {
        String imageLocation = getClass().getResource(ICON_PATH).toString();
        this.setGraphic(new ImageView(imageLocation));

        ButtonType inviteButtonType = new ButtonType("Invite", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().addAll(inviteButtonType, ButtonType.CANCEL);


        Spinner boardSizeSpinner = new Spinner(10, 30, 15);
        boardSizeSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        boardSizeSpinner.setEditable(true);

        Spinner timeoutSpinner = new Spinner(100, 5000, 500, 100);
        timeoutSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        timeoutSpinner.setEditable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 100, 10, 10));
        grid.add(new Label("Board size:"), 0, 0);
        grid.add(boardSizeSpinner, 1, 0);
        grid.add(new Label("Step timeout:"), 0, 1);
        grid.add(timeoutSpinner, 1, 1);

        ObservableList<String> items = FXCollections.observableArrayList(invitedPlayers);
        ListView invitedList = new ListView();
        invitedList.setItems(items);
        invitedList.setEditable(false);
        invitedList.setPrefHeight(120);
        invitedList.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());

        VBox vbox = new VBox();
        vbox.getChildren().addAll(new Label("Invited players:"), invitedList);

        this.getDialogPane().setContent(new VBox(grid, vbox));

        this.setResultConverter(dialogButton ->
        {
            if (dialogButton == inviteButtonType)
            {
                String sizeString = boardSizeSpinner.getValue().toString();
                String timeoutString = timeoutSpinner.getValue().toString();
                return new MatchConstraints(Integer.parseInt(sizeString), Integer.parseInt(timeoutString));
            }

            return null;
        });
    }

    public static Dialog<MatchConstraints> create(List<String> invitedPlayers)
    {
        return new InvitationDialog(invitedPlayers);
    }
}
