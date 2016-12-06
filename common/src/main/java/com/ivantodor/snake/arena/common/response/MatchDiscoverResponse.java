package com.ivantodor.snake.arena.common.response;

import com.ivantodor.snake.arena.common.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Todorovic
 */
public class MatchDiscoverResponse extends Message
{
    public static final String TYPE = "matchDiscoverResponse";
    private String matchId;
    private List<String> players;

    public MatchDiscoverResponse()
    {
        super(TYPE);
        this.matchId = "/";
        this.players = new ArrayList<String>();
    }

    public MatchDiscoverResponse(String matchId, List<String> players)
    {
        super(TYPE);
        this.matchId = matchId;
        this.players = players;
    }

    public String getMatchId()
    {
        return matchId;
    }

    public List<String> getPlayers()
    {
        return players;
    }
}
