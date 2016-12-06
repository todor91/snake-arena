package com.ivantodor.snake.arena.client.view;

import com.ivantodor.snake.arena.common.request.MatchInvitationRequest;

import java.util.List;

/**
 * @author Ivan Todorovic
 */
public interface PlayerListView
{
    void setPlayers(List<String> playerName);

    void processInvitation(MatchInvitationRequest request);
}
