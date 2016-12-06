package com.ivantodor.snake.arena.common.response;

import com.ivantodor.snake.arena.common.Message;
import com.ivantodor.snake.arena.common.model.MatchState;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Ivan Todorovic
 */
public class MatchStatusResponse extends Message
{
    public static final String TYPE = "matchStatusResponse";

    private int size = 0;
    private String matchId = "";
    private Point foodPosition = new Point();
    private Map<String, List<Point>> snakes = new HashMap<String, List<Point>>();;
    private Map<String, Integer> scoreMap = new HashMap<String, Integer>();
    private MatchState matchState = MatchState.INIT;

    public MatchStatusResponse()
    {
        super(TYPE);
    }

    public MatchStatusResponse(String matchId, int size, Point food, Map<String, List<Point>> snakes, Map<String, Integer> scoreMap, MatchState matchState)
    {
        this();
        this.matchId = matchId;
        this.size = size;
        this.foodPosition = food;
        this.snakes = snakes;
        this.matchState = matchState;
        this.scoreMap = scoreMap;
    }

    public int getSize()
    {
        return size;
    }

    public String getMatchId()
    {
        return matchId;
    }

    public Point getFoodPosition()
    {
        return foodPosition;
    }

    public Map<String, List<Point>> getSnakes()
    {
        return snakes;
    }

    public Map<String, Integer> getScores()
    {
        return scoreMap;
    }

    public MatchState getMatchState()
    {
        return matchState;
    }
}