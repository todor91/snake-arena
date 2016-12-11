package com.ivantodor.snake.arena.client.view.helper;

import javafx.scene.paint.Color;

/**
 * @author Ivan Todorovic
 */
public class HashColorGenerator implements ColorGenerator
{
    @Override
    public String getHexColor(String value)
    {
        return String.format("#%X", value.hashCode() + 4096);
    }

    @Override
    public Color getColor(String value)
    {
        Color tempColor = Color.web(getHexColor(value), 1);
        return Color.color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), 1.0);
    }
}
