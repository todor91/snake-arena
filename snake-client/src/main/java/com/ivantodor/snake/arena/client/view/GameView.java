package com.ivantodor.snake.arena.client.view;

import com.ivantodor.snake.arena.common.response.MatchStatusResponse;

/**
 * @author Ivan Todorovic
 */
public interface GameView
{
    void processMatchStatus(MatchStatusResponse statusResponse);
}
