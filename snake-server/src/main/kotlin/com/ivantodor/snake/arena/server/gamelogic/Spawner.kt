package com.ivantodor.snake.arena.server.gamelogic

import java.awt.Point
import java.util.*

/**
 * @author Ivan Todorovic
 */

interface Spawner {
    /**
     * This method will be called when the match starts, and it should properly position snakes on the board
     */
    fun spawnSnakes(board: Board, players: List<String>): Map<String, Snake>
}

class ParallelSpawner : Spawner {
    override fun spawnSnakes(board: Board, players: List<String>): Map<String, Snake> {
        if(board.size - 2 < players.size)
            throw RuntimeException("Player count too high")

        var toRight = true
        var row = 1

        val step = board.size - 3 / (players.size - 1)
        players.forEach { player ->
            val col = if(toRight == true)
                3
            else
                board.size - 1 - 3

            val snake = spawnSnake(Point(col, row), toRight)
            row = row + step
            toRight = !toRight

            board.addSnake(player, snake)
        }

        return mapOf()
    }

    private fun spawnSnake(headPoint: Point, rightFacing: Boolean): Snake {
        val list = LinkedList<Point>()
        list.add(headPoint)
        if(rightFacing) {
            list.add(Point(headPoint.x - 1, headPoint.y))
            list.add(Point(headPoint.x - 2, headPoint.y))
        } else {
            list.add(Point(headPoint.x + 1, headPoint.y))
            list.add(Point(headPoint.x + 2, headPoint.y))
        }

        return Snake(list)
    }


}

class RandomParallelSpawner : Spawner {
    override fun spawnSnakes(board: Board, players: List<String>): Map<String, Snake> {
        if(board.size - 2 < players.size)
            throw RuntimeException("Player count too high")

        val availableRow = mutableSetOf<Int>()
        (0 until board.size).forEach { availableRow.add(it) }

        players.forEach { player ->
            val availableRowArray = availableRow.toTypedArray()
            val randomRow = availableRowArray[Random().nextInt(availableRow.size)]
            availableRow.remove(randomRow)
            val toRight = randomRow % 2 != 0

            val col = if(toRight == true) 3 else board.size - 1 - 3

            val snake = spawnSnake(Point(col, randomRow), toRight)

            board.addSnake(player, snake)
        }

        return mapOf()
    }

    private fun spawnSnake(headPoint: Point, rightFacing: Boolean): Snake {
        val list = LinkedList<Point>()
        list.add(headPoint)
        if(rightFacing) {
            list.add(Point(headPoint.x - 1, headPoint.y))
            list.add(Point(headPoint.x - 2, headPoint.y))
        } else {
            list.add(Point(headPoint.x + 1, headPoint.y))
            list.add(Point(headPoint.x + 2, headPoint.y))
        }

        return Snake(list)
    }


}