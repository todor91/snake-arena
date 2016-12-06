package com.ivantodor.snake.arena.common;

import com.ivantodor.snake.arena.common.model.Direction;

/**
 * @author Ivan Todorovic
 */
public class MoveAction extends Message
{
    public static final String TYPE = "moveAction";

    private String matchId;
    private Direction direction;

    public MoveAction()
    {
        super(TYPE);
    }

    public MoveAction(String matchId, Direction direction)
    {
        this();
        this.matchId = matchId;
        this.direction = direction;
    }

    public String getMatchId()
    {
        return matchId;
    }

    public Direction getDirection()
    {
        return direction;
    }
}
