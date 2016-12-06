package com.ivantodor.snake.arena.common.request;

import com.ivantodor.snake.arena.common.Message;

/**
 * @author Ivan Todorovic
 */
public class PlayerListRequest extends Message
{
    public static String TYPE = "playerListRequest";

    public PlayerListRequest()
    {
        super(TYPE);
    }
}
