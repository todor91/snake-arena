package com.ivantodor.snake.arena.server.gamelogic

import com.ivantodor.snake.arena.common.model.Direction
import com.ivantodor.snake.arena.common.model.MatchState
import mu.KLogging
import java.awt.Point

/**
 * @author Ivan Todorovic
 */
/**
 * The basic idea is that each player registers its move, and by calling 'executeStep' each move is executed, so
 * that each player made an action. When 'executeStep' is called 'step' counter is incremented
 */
class Match(private val players: List<String>, val board: Board, val rules: Rules, private val spawner: Spawner) {
    companion object : KLogging()

    var state: MatchState = MatchState.INIT
        private set

    var step: Int = 0
        private set

    // Registered moves to be executed in the next step
    private val playerMoves: MutableMap<String, Direction> = mutableMapOf()
    // List of all invalid snakes in the previous step
    private val lastStepInvalid: MutableMap<String, Snake> = mutableMapOf()
    // Accumulated scores calculated from each step
    val playerScores: MutableMap<String, Int> = mutableMapOf()

    fun resetMatch(): Unit {
        initMatchInternals()
        spawner.spawnSnakes(board, players)

        state = MatchState.INIT
        step = 0
    }

    fun startMatch(): MatchStatus {
        state = MatchState.ACTIVE

        return getMatchStatus()
    }

    fun registerMove(snakeName: String, direction: Direction): Boolean {
        if(board.getSnake(snakeName) == null)
            return false

        playerMoves[snakeName] = direction
        return true
    }

    fun executeStep(): MatchStatus {
        // Move all snakes according to the registered moves
        playerMoves.forEach { playerMove ->
            val snake = board.getSnake(playerMove.key)
            snake?.move(playerMove.value)
        }

        // remove invalid snakes
        // resulting board contains only valid snakes
        val updateStatus = rules.updateBoard(board)

        updateStatus.playerScoreIncrements.forEach {
            val currentScore = playerScores[it.key] ?: 0
            playerScores.put(it.key, it.value + currentScore)
        }

        // todo test is this is ok
        if(rules.isActive(board, playerScores) == false) {
            val maxScore = playerScores.values.max() ?: 0
            if(playerScores.values.filter { it == maxScore }.size > 1)
                state = MatchState.DRAW
            else
                state = MatchState.DONE
        }


        lastStepInvalid.clear()
        lastStepInvalid.putAll(updateStatus.invalidSnakes)

        step++

        return getMatchStatus()
    }

    fun getMatchStatus(): MatchStatus {
        return MatchStatus(step, state, board.getFoodPosition(), board.getSnakesByName(), lastStepInvalid)
    }

    private fun initMatchInternals() {
        players.forEach { player ->
            playerMoves.put(player, Direction.FORWARD)
            playerScores.put(player, 0)
            lastStepInvalid.clear()
        }
    }
}

data class MatchStatus(val step: Int,
                       val state: MatchState,
                       val food: Point,
                       val activeSnakes:Map<String, Snake>,
                       val currentlyInvalid: Map<String, Snake>)

class MatchException(override val message: String) : RuntimeException()