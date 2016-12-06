package com.ivantodor.snake.arena.client.view.impl;

import com.ivantodor.snake.arena.client.controller.GameManager;
import com.ivantodor.snake.arena.client.view.ConnectionView;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import javax.swing.*;

/**
 * @author Ivan Todorovic
 */
public class ConnectionPane extends HBox implements ConnectionView
{
    public static final String DEFAULT_HOST = "ws://localhost:8080/snakearena/";
    public static final String DEFAULT_NAME = "TestName";

    private TextField hostField;
    private TextField nameField;
    private Button connectDisconnectButton = new Button("Connect");

    public ConnectionPane()
    {
        super(15.0); //Adding spacing

        this.hostField = new TextField(DEFAULT_HOST);
        this.nameField = new TextField(DEFAULT_NAME);
        setupUI();
    }

    public void setupUI()
    {
        hostField.setMinWidth(350);
        nameField.setMinWidth(150);
        this.getChildren().addAll(new Label("Host"), hostField, new Label("Name"), nameField, connectDisconnectButton);
        this.setAlignment(Pos.CENTER);
        setStyle("-fx-border-color: black");


        connectDisconnectButton.setOnAction(e -> connectDisconnectAction());
    }

    public void setConnected(boolean flag)
    {
        if (flag)
        {
            connectDisconnectButton.setText("Disconnect");
            hostField.setDisable(true);
            nameField.setDisable(true);
        }
        else
        {
            connectDisconnectButton.setText("Connect");
            hostField.setDisable(false);
            nameField.setDisable(false);
        }
    }

    public void connectionRejected(String message)
    {
        JOptionPane.showMessageDialog(null, message);
        setConnected(false);
    }

    private void connectDisconnectAction()
    {
        if (connectDisconnectButton.getText().equals("Connect"))
        {
            GameManager.getInstance().connectToGameServer(hostField.getText(), nameField.getText());
            setConnected(true);
        }
        else
        {
            setConnected(false);
            GameManager.getInstance().disconnectFromGameServer();
        }
        setConnected(true);
    }

}
