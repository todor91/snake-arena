package com.ivantodor.snake.arena.client.view;

import com.ivantodor.snake.arena.common.model.MatchState;

import java.util.Map;

/**
 * @author Ivan Todorovic
 */
public interface StatusView
{
    void updateStatusView(String matchId, MatchState state, Map<String, Integer> scores);
}
