package com.ivantodor.snake.arena.client.view.impl;

import com.ivantodor.snake.arena.client.view.GameView;
import com.ivantodor.snake.arena.client.view.helper.CanvasPane;
import com.ivantodor.snake.arena.client.view.helper.ColorGenerator;
import com.ivantodor.snake.arena.client.view.helper.HashColorGenerator;
import com.ivantodor.snake.arena.common.response.MatchStatusResponse;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Ivan Todorovic
 */
public class GamePane extends BorderPane implements GameView
{
    private CanvasPane canvasPane = new CanvasPane(200, 200);

    private int rows = 10;
    private int cols = 10;
    private int horizontalMargin = 20;
    private int verticalMargin = 20;
    private Point food = new Point(-20, -20);

    private Map<String, List<Point>> snakes = new HashMap<>();

    private ColorGenerator colorGenerator = new HashColorGenerator();
    @Override
    public void resize(double width, double height)
    {
        super.resize(width, height);
        canvasPane.getCanvas().setWidth(width);
        canvasPane.getCanvas().setHeight(height);

        paint();
    }

    public GamePane()
    {
        GraphicsContext gc = canvasPane.getCanvas().getGraphicsContext2D();
        gc.setFill(Color.GREEN);

        setCenter(canvasPane);
        this.setStyle("-fx-background-color: #" + "F5F5DC");
        paint();
    }

    private void paint()
    {
        GraphicsContext gc = canvasPane.getCanvas().getGraphicsContext2D();
        gc.clearRect(0, 0, canvasPane.getCanvas().getWidth(), canvasPane.getCanvas().getHeight());

        drawBackgroundGrid(gc);
        drawAllSnakes(gc);
        drawFood(gc);
    }

    private void drawBackgroundGrid(GraphicsContext gc)
    {
        gc.setLineWidth(2);
        gc.setStroke(Color.BLUE);
        Point2D topLeft = new Point2D(horizontalMargin, verticalMargin);
        Point2D bottomRight = new Point2D(canvasPane.getCanvas().widthProperty().get() - horizontalMargin , canvasPane.getCanvas().heightProperty().get() - verticalMargin);

        double rectWidth = (bottomRight.getX() - topLeft.getX()) / cols;
        double rectHeight = (bottomRight.getY() - topLeft.getY()) / rows;

        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
            {
                double posX = topLeft.getX() + j * rectWidth;
                double posY = topLeft.getY() + i * rectHeight;

                gc.strokeRect(posX, posY, rectWidth, rectHeight);
            }
    }

    private void drawSnake(GraphicsContext gc, List<Point2D> points, Color color)
    {
        gc.setStroke(color);
        gc.setLineWidth(6);
        gc.setFill(color);
        Point2D prev = translatePoint(points.get(0));
        gc.fillOval(prev.getX()- 10, prev.getY() - 10, 20, 20);

        for (int i = 1; i < points.size(); i++)
        {
            Point2D current = translatePoint(points.get(i));
            gc.strokeLine(prev.getX(), prev.getY(), current.getX(), current.getY());

            prev = current;
        }
    }

    private void drawAllSnakes(GraphicsContext gc)
    {
        for (Map.Entry<String, List<Point>> entry : snakes.entrySet()) {
            List<Point2D> value = new LinkedList<>();
            for (Point p: entry.getValue())
            {
                value.add(new Point2D(p.getX(), p.getY()));
            }

            Color snakeColor = colorGenerator.getColor(entry.getKey());
            drawSnake(gc, value, snakeColor);
        }
    }

    private void drawFood(GraphicsContext gc)
    {
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.MAGENTA);

        Point2D position = translatePoint(new Point2D(food.getX(), food.getY()));

        gc.fillOval(position.getX() - 10, position.getY() - 10, 20, 20);
    }

    private Point2D translatePoint(Point2D point)
    {
        Point2D topLeft = new Point2D(horizontalMargin, verticalMargin);
        Point2D bottomRight = new Point2D(canvasPane.getCanvas().widthProperty().get() - horizontalMargin , canvasPane.getCanvas().heightProperty().get() - verticalMargin);
        double rectWidth = (bottomRight.getX() - topLeft.getX()) / cols;
        double rectHeight = (bottomRight.getY() - topLeft.getY()) / rows;


        double x = topLeft.getX() + point.getX() * rectWidth + rectWidth / 2;
        double y = topLeft.getY() + point.getY() * rectHeight + rectHeight / 2;

        return new Point2D(x, y);
    }

    @Override
    public void processMatchStatus(MatchStatusResponse statusResponse)
    {
        snakes.clear();
        rows = statusResponse.getSize();
        cols = statusResponse.getSize();
        snakes = statusResponse.getSnakes();
        food = statusResponse.getFoodPosition();

        paint();
    }
}