package com.ivantodor.snake.arena.client.model;

import java.awt.*;
import java.util.List;

/**
 * @author Ivan Todorovic
 */
public class Board
{
    private int size;
    private Snake playerSnake;
    private List<Snake> enemySnakes;
    private Point foodPosition;

    public Board(int size, Snake playerSnake, List<Snake> enemySnakes, Point foodPosition)
    {
        this.size = size;
        this.playerSnake = playerSnake;
        this.enemySnakes = enemySnakes;
        this.foodPosition = foodPosition;
    }

    public int getSize()
    {
        return size;
    }

    public Snake getPlayerSnake()
    {
        return playerSnake;
    }

    public List<Snake> getEnemySnakes()
    {
        return enemySnakes;
    }

    public Point getFoodPosition()
    {
        return foodPosition;
    }
}
