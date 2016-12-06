package com.ivantodor.snake.arena.server.gamelogic

import mu.KLogging
import java.awt.Point
import java.util.*

/**
 * @author Ivan Todorovic
 */

data class UpdateStatus(val invalidSnakes: Map<String, Snake>,
                        val playerScoreIncrements: Map<String, Int>)

interface Rules {
    /**
     * Returns whether the game is active or not
     */
    fun isActive(board: Board, currentScores: Map<String, Int>): Boolean

    /**
     * Updates the board according to the rules(for example removes the snakes, translates snake position on the other side of the board,
     * calculates the scores, spawns food...)
     */
    fun updateBoard(board: Board): UpdateStatus
}


class MaxLengthRules() : Rules {
    companion object : KLogging()

    override fun isActive(board: Board, currentScores: Map<String, Int>): Boolean {
        if(board.getSnakes().size == 0) // no snakes on board: game has ended
            return false

        // if there is one snake on the board, and it hasn't scored the max score
        // the game is still active, until it reaches max score, or it dies
        if(board.getSnakes().size == 1) {
            val snakeName = board.getSnakesByName().keys.first()
            val snakeScore = currentScores[snakeName] ?: -1

            val (maxScore, maxNames) = getMaxScore(currentScores)

            // If there is more that one snake with max score, continue the game
            if(maxNames.size > 1)
                return true

            // If the only active snake is the snake with max score, finish the game
            if(snakeName in maxNames)
                return false
        }

        return true
    }

    override fun updateBoard(board: Board): UpdateStatus {
        val invalidSnakes = getInvalidSnakes(board)

        invalidSnakes.forEach {
            board.removeSnake(it.key)
        }

        val growSnakes = growSnakes(board)

        if(growSnakes.isEmpty() == false)
            spawnAnotherFood(board)

        return UpdateStatus(invalidSnakes, growSnakes)
    }

    // todo multiple snakes over one point
    private fun growSnakes(board: Board): Map<String, Int> {
        var scoredSnake: String? = null

        for((name, snake) in board.getSnakesByName()){
            if(snake.getHead().equals(board.getFoodPosition())) {
                snake.appendTail()
                scoredSnake = name
                break
            }
        }

        val result = mutableMapOf<String, Int>()
        if(scoredSnake != null) {
            result.put(scoredSnake, 1)
        }

        return result
    }

    private fun getInvalidSnakes(board: Board): Map<String, Snake> {
        val selfCollisionSnakes = board.getSnakesByName().filter { it.value.selfCollision() }
        val mutualColission = collisionSnakes(board)
        val wallCollision = wallCollisionSnakes(board)

        return selfCollisionSnakes + mutualColission + wallCollision
    }

    // todo Pls optimize this asap :-(
    private fun collisionSnakes(board: Board): Map<String, Snake> {
        val collision: MutableMap<String, Snake> = mutableMapOf()
        board.getSnakesByName().forEach { snake ->
            val otherSnakes = board.getSnakesByName().filterNot { it.key == snake.key }
            for ((key, value) in otherSnakes) {
                if(value.pointList.contains(snake.value.getHead())) {
                    collision.put(snake.key, snake.value)
                    break
                }
            }
        }
        return collision
    }

    private fun wallCollisionSnakes(board: Board): Map<String, Snake> {
        fun wallHit(p: Point) = p.x < 0 || p.x >= board.size || p.y < 0 || p.y >= board.size

        return board.getSnakesByName().filter { wallHit(it.value.getHead()) }
    }

    private fun getMaxScore(currentScores: Map<String, Int>): Pair<Int, Set<String>> {
        val maxScore = currentScores.maxBy { it.value }?.value ?: 0
        val maxNames = currentScores.filter { it.value == maxScore }.keys

        return Pair(maxScore, maxNames)
    }

    private fun spawnAnotherFood(board: Board) {
        do {
            val x = Random().nextInt(board.size)
            val y = Random().nextInt(board.size)
        } while(board.setFoodPosition(Point(x, y)) == false)
    }
}