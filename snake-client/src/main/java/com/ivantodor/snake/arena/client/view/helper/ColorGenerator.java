package com.ivantodor.snake.arena.client.view.helper;

import javafx.scene.paint.Color;

/**
 * @author Ivan Todorovic
 */
public interface ColorGenerator
{
    String getHexColor(String value);

    Color getColor(String value);
}
