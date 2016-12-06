package com.ivantodor.snake.arena.server.gamelogic

import com.ivantodor.snake.arena.common.model.Direction
import java.awt.Point

/**
 * @author Ivan Todorovic
 */
interface Board {
    val size: Int

    fun getPlayers(): List<String>

    fun getSnakes(): List<Snake>

    fun getSnake(name: String): Snake?

    fun getSnakesByName(): Map<String, Snake>

    fun addSnake(name: String, snake: Snake): Boolean

    fun getFoodPosition(): Point

    fun setFoodPosition(position: Point): Boolean

    fun removeSnake(name: String): Snake?
}

class MatrixBoard(override val size: Int) : Board {
    private val playerSnakeMap: MutableMap<String, Snake> = mutableMapOf()
    private var foodPosition: Point = Point(size / 2, size / 2)

    fun issueMoveCommand(name: String, direction: Direction) {
        val snake = playerSnakeMap.get(name)

        val tailPosition = snake?.getTail()

        snake?.move(direction)
    }

    override fun getPlayers(): List<String> {
        return playerSnakeMap.keys.toList()
    }

    override fun getSnakes(): List<Snake> {
        return playerSnakeMap.values.toList()
    }

    override fun getFoodPosition(): Point {
        return foodPosition
    }

    override fun getSnake(name: String): Snake? {
        return playerSnakeMap[name]
    }

    override fun getSnakesByName(): Map<String, Snake> {
        return playerSnakeMap
    }

    override fun addSnake(name: String, snake: Snake): Boolean {
        if(playerSnakeMap.containsKey(name))
            return false

        playerSnakeMap.put(name, snake)
        return true
    }

    override fun setFoodPosition(position: Point): Boolean {
        val emptyPosition = getSnakes().all { it.pointList.contains(position) == false }
        val insideMatrix = position.x >= 0 && position.x < size && position.y >= 0 && position.y < size

        if(emptyPosition && insideMatrix && position.equals(foodPosition) == false) {
            foodPosition = position
            return true
        }
        return false
    }

    override fun removeSnake(name: String): Snake? = playerSnakeMap.remove(name)
}