package com.ivantodor.snake.arena.client.model;

import java.awt.*;
import java.util.List;

/**
 * @author Ivan Todorovic
 */
public class Snake
{
    private List<Point> snakePoints;

    public Snake(List<Point> points)
    {
        this.snakePoints = points;
    }

    public Point getHead()
    {
        return snakePoints.get(0);
    }

    public List<Point> allPoints()
    {
        return snakePoints;
    }
}
