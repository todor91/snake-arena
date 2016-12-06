package com.ivantodor.snake.arena.client.controller;

import com.ivantodor.snake.arena.client.model.Board;
import com.ivantodor.snake.arena.common.model.Direction;

/**
 * @author Ivan Todorovic
 */
public interface SnakeAI
{
    Direction move(Board board);
}
