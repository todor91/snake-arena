package com.ivantodor.snake.arena.common.response;

import com.ivantodor.snake.arena.common.Message;

/**
 * @author Ivan Todorovic
 */
public class FinishedMatchResponse extends Message
{
    public static final String TYPE = "finishedMatchResponse";
    private String matchId;

    public FinishedMatchResponse()
    {
        super(TYPE);
        matchId = "";
    }
    public FinishedMatchResponse(String matchId)
    {
        super(TYPE);
        this.matchId = matchId;
    }

    public String getMatchId()
    {
        return matchId;
    }
}
