package com.ivantodor.snake.arena.client.websocket;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author Ivan Todorovic
 */
public interface JsonMessageListener
{
    void onMessage(String type, JsonNode message);
}
