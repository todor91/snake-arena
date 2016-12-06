package com.ivantodor.snake.arena.common.response;

import com.ivantodor.snake.arena.common.Message;

/**
 * @author Ivan Todorovic
 */
public class MatchInvitationResponse extends Message
{
    public static final String TYPE = "matchInvitationResponse";

    private String invitationId;
    private boolean response;

    public MatchInvitationResponse()
    {
        super(TYPE);
        this.response = false;
    }

    public MatchInvitationResponse(String invitationId, boolean response)
    {
        this();
        this.invitationId = invitationId;
        this.response = response;
    }

    public boolean isResponse()
    {
        return response;
    }

    public String getInvitationId()
    {
        return invitationId;
    }
}
