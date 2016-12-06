package com.ivantodor.snake.arena.common.response;

import com.ivantodor.snake.arena.common.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ivan Todorovic
 */
public class PlayerListResponse extends Message
{
    public static final String TYPE = "playerListResponse";
    private List<String> players;

    public PlayerListResponse()
    {
        super(TYPE);
        this.players = new ArrayList<String>();
    }

    public PlayerListResponse(List<String> players)
    {
        super(TYPE);
        this.players = players;
    }

    public List<String> getPlayers()
    {
        return players;
    }
}
