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
        StringBuilder finalString = new StringBuilder(value);
        if(value.length() < 3)
        {
            finalString.append(finalString.toString());
            finalString.append(finalString.toString());
            finalString.append(finalString.toString());
        }
        return String.format("#%X", finalString.toString().hashCode());
    }

    @Override
    public Color getColor(String value)
    {
        Color tempColor = Color.web(getHexColor(value), 1);
        return Color.color(tempColor.getRed(), tempColor.getGreen(), tempColor.getBlue(), 1.0);
    }
}
