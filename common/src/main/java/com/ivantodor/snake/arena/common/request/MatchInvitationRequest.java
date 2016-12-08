package com.ivantodor.snake.arena.common.request;

import com.ivantodor.snake.arena.common.Message;
import com.ivantodor.snake.arena.common.model.MatchConstraints;

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
    private List<String> invitedPlayers = new ArrayList<String>();
    private MatchConstraints matchConstraints;

    public MatchInvitationRequest()
    {
        super(TYPE);
    }

    public MatchInvitationRequest(List<String> invitedPlayers, MatchConstraints matchConstraints)
    {
        this();
        this.matchConstraints = matchConstraints;
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

    public MatchConstraints getMatchConstraints()
    {
        return matchConstraints;
    }
}
