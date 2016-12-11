package com.ivantodor.snake.arena.client.websocket;

import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;


public class WebsocketClient extends Endpoint
{
    private Logger logger = LoggerFactory.getLogger(getClass().getName());
    private MessageHandler messageHandler;
    private Session session; // thread safe
    //Each web socket session uses no more than one thread at a time to call its MessageHandlers. !!


    public void connect(String remoteHost) throws WebsocketClientException
    {
        final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();

        ClientManager client = ClientManager.createClient();
        try
        {
            URI connectionURI = new URI(remoteHost);
            session = client.connectToServer(this, cec, connectionURI);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage());
            throw new WebsocketClientException(e.getMessage());
        }
    }

    public void setMessageHandler(MessageHandler messageHandler)
    {
        this.messageHandler = messageHandler;
    }

    public void sendMessage(String message)
    {
        if (session != null)
            session.getAsyncRemote().sendText(message);
        else
            throw new RuntimeException("Session not initialized");
    }

    @Override
    public void onOpen(Session session, EndpointConfig config)
    {
        logger.info("Connected!!!");
        try
        {
            session.addMessageHandler(messageHandler);
        }
        catch (IllegalStateException e)
        {
            logger.error("Binding message handler failed", e);
        }
    }

    public void disconnect()
    {
        if(session == null)
            return;

        try
        {
            session.close();
            session = null;
        }
        catch (IOException e)
        {
            logger.error("Error during websocket closing", e);
        }
    }

    public boolean isOpen()
    {
        return session != null && session.isOpen();
    }
}