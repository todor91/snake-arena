package com.ivantodor.snake.arena.client.view;

/**
 * @author Ivan Todorovic
 */
public interface ConnectionView
{
    void setConnected(boolean flag);

    void connectionRejected(String message);
}
