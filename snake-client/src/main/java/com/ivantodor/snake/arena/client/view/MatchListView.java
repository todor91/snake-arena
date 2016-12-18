package com.ivantodor.snake.arena.client.view;

/**
 * @author Ivan Todorovic
 */
public interface MatchListView
{
    void addMatch(String matchId);

    void removeMatch(String matchId);

    void removeAll();
}
