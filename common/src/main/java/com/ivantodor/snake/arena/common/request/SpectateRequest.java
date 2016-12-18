package com.ivantodor.snake.arena.common.request;

import com.ivantodor.snake.arena.common.Message;

/**
 * @author Ivan Todorovic
 */
public class SpectateRequest extends Message
{
    public static String TYPE = "spectateRequest";

    private String matchId;

    public SpectateRequest(String matchId)
    {
        super(TYPE);
        this.matchId = matchId;
    }

    public String getMatchId()
    {
        return matchId;
    }
}
