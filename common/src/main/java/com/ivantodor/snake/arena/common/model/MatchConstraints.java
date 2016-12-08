package com.ivantodor.snake.arena.common.model;

/**
 * @author Ivan Todorovic
 */
public class MatchConstraints
{
    private int boardSize;
    private int stepTimeout;

    public MatchConstraints()
    {
        this.boardSize = 10;
        this.stepTimeout = 1000;
    }

    public MatchConstraints(int boardSize, int stepTimeout)
    {
        this.boardSize = boardSize;
        this.stepTimeout = stepTimeout;
    }

    public int getBoardSize()
    {
        return boardSize;
    }

    public int getStepTimeout()
    {
        return stepTimeout;
    }
}
