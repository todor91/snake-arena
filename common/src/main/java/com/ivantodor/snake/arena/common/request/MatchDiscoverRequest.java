package com.ivantodor.snake.arena.common.request;

import com.ivantodor.snake.arena.common.Message;

/**
 * @author Ivan Todorovic
 */
public class MatchDiscoverRequest extends Message
{
    public static String TYPE = "matchDiscoverRequest";

    public MatchDiscoverRequest()
    {
        super(TYPE);
    }
}
