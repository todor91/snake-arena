package com.ivantodor.snake.arena.common.request;

import com.ivantodor.snake.arena.common.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Ivan Todorovic
 */
public class MatchInvitationRequest extends Message
{
    public static final String TYPE = "matchInvitation";

    private String invitationId;
    private int boardSize;
    private List<String> invitedPlayers = new ArrayList<String>();

    public MatchInvitationRequest()
    {
        super(TYPE);
    }

    public MatchInvitationRequest(int boardSize, List<String> invitedPlayers)
    {
        this();
        this.boardSize = boardSize;
        this.invitationId = UUID.randomUUID().toString();
        this.invitedPlayers = invitedPlayers;
    }

    public List<String> getInvitedPlayers()
    {
        return invitedPlayers;
    }

    public String getInvitationId()
    {
        return invitationId;
    }

    public int getBoardSize()
    {
        return boardSize;
    }
}
