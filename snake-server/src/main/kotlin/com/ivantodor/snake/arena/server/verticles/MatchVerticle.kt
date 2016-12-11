package com.ivantodor.snake.arena.server.verticles

import com.ivantodor.snake.arena.common.MoveAction
import com.ivantodor.snake.arena.common.model.MatchConstraints
import com.ivantodor.snake.arena.common.model.MatchState
import com.ivantodor.snake.arena.common.response.MatchDiscoverResponse
import com.ivantodor.snake.arena.common.response.MatchStatusResponse
import com.ivantodor.snake.arena.server.gamelogic.*
import com.ivantodor.snake.arena.server.helper.onFailure
import com.ivantodor.snake.arena.server.helper.onSuccess
import io.vertx.core.AbstractVerticle
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import mu.KLogging

/**
 * @author Ivan Todorovic
 */
class MatchVerticle(val matchId: String, val clients: List<String>, val matchConstraints: MatchConstraints) : AbstractVerticle() {
    companion object : KLogging()

    val board = MatrixBoard(matchConstraints.boardSize)
    val rules = MaxLengthRules()
    val match = Match(clients, board, rules, RandomParallelSpawner())

    private var clientResponses: MutableSet<String> = mutableSetOf()

    private var timerId: Long = 0
    private val preemptiveExecutionTimer: Long = Math.min(200, matchConstraints.stepTimeout.toLong())

    override fun start() {
        // Reset the board, and place the snakes
        match.resetMatch()

        // Call "main loop"
        timedUpdateAndReport()

        // Take care of clients' MoveActions
        registerMoveActionListener()

        // Process match discovery message
        registerMatchDiscoveryListener()
    }

    private fun timedUpdateAndReport() {
        when (match.state) {
            MatchState.INIT -> {
                val startingStatus = match.startMatch()
                broadcastMatchStatus(startingStatus)
            }

            MatchState.ACTIVE -> {
                val status = match.executeStep()
                broadcastMatchStatus(status)
            }

            MatchState.DRAW -> {
                match.resetMatch()
                clientResponses.clear()
            }

            else -> undeployVerticle()
        }

        if(match.state != MatchState.DONE)
            withMatchTimer { timedUpdateAndReport() }
    }

    private fun registerMoveActionListener() {
        vertx.eventBus().consumer<JsonObject>(Address.Match.action(matchId)) { jsonMsg ->
            if(match.state == MatchState.ACTIVE) {
                val moveAction = Json.decodeValue(jsonMsg.body().toString(), MoveAction::class.java)
                val clientId = jsonMsg.headers()["clientId"]

                match.registerMove(clientId, moveAction.direction)
                clientResponses.add(clientId)

                // If all active players supplied their answers, execute the move
                val activeSnakeNames = match.board.getPlayers()
                if (clientResponses.containsAll(activeSnakeNames)) {
                    vertx.cancelTimer(timerId)
                    clientResponses.clear()

                    // todo take care of situation when moveActions came just before the "main loop" timer event
                    vertx.setTimer(preemptiveExecutionTimer) {
                        timedUpdateAndReport()
                    }
                }
            }
        }
    }

    private fun registerMatchDiscoveryListener() {
        vertx.eventBus().consumer<JsonObject>(Address.Match.Discover) { msg ->
            val discover = MatchDiscoverResponse(matchId, clients)

            val x = JsonObject(Json.encode(discover))
            vertx.eventBus().send(Address.Client.clientHanlder(msg.body().getString("clientId")), x)
        }
    }

    private fun undeployVerticle() {
        logger.info("Undeploying match verticle '$matchId'")
        vertx.undeploy(deploymentID()) { result ->
            result.onSuccess { logger.info("Undeployment successful for match '$matchId'") }
            result.onFailure { logger.error("Failed to undeploy match verticle for '$matchId'") }
        }
    }

    private fun broadcastMatchStatus(matchStatus: MatchStatus) {
        val validSnakes = matchStatus.activeSnakes.map { Pair(it.key, it.value.pointList) }.toMap()
        val invalidSnakes = matchStatus.currentlyInvalid.map { Pair(it.key, it.value.pointList) }.toMap()

        val statusReport = MatchStatusResponse(matchId, board.size, matchStatus.food,
                validSnakes + invalidSnakes, match.playerScores, match.state)

        // all clients including spectators and clients
        val allDestinationClients = clients

        val statusReportJson = JsonObject(Json.encode(statusReport))

        allDestinationClients.forEach {
            vertx.eventBus().send(Address.Client.clientHanlder(it), statusReportJson)
        }
    }

    private fun withMatchTimer(f: () -> Unit) {
        timerId = vertx.setTimer(matchConstraints.stepTimeout.toLong()) {
            f()
        }
    }
}