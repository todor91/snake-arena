package com.ivantodor.snake.arena.server.verticles

import com.ivantodor.snake.arena.common.MoveAction
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

data class MatchConstraints(val boardSize: Int)

class MatchVerticle(val matchId: String, val clients: List<String>, matchConstraints: MatchConstraints) : AbstractVerticle() {
    companion object : KLogging()

    val board = MatrixBoard(matchConstraints.boardSize)
    val rules = MaxLengthRules()
    val match = Match(clients, board, rules, RandomParallelSpawner())

    override fun start() {

        val startStatus = match.startGame()
        broadcastMatchStatus(startStatus)

        periodicUpdateAndReport()

        // Store move actions
        vertx.eventBus().consumer<JsonObject>(Address.Match.action(matchId)) { jsonMsg ->
            val moveAction = Json.decodeValue(jsonMsg.body().toString(), MoveAction::class.java)

            match.registerMove(jsonMsg.headers()["clientId"], moveAction.direction)
        }

        // Process match discovery message
        vertx.eventBus().consumer<JsonObject>(Address.Match.Discover) { msg ->
            val discover = MatchDiscoverResponse(matchId, clients)

            val x = JsonObject(Json.encode(discover))
            vertx.eventBus().send(Address.Client.clientHanlder(msg.body().getString("clientId")), x)

        }
    }

    private fun periodicUpdateAndReport() {
        vertx.setPeriodic(1000) {
            when (match.state) {
                MatchState.ACTIVE -> {
                    val status = match.executeStep()

                    broadcastMatchStatus(status)
                }
                MatchState.DRAW -> match.startGame()
                else -> undeployVerticle()
            }
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

    private fun undeployVerticle() {
        logger.info("Undeploying match verticle '$matchId'")
        vertx.undeploy(deploymentID()) { result ->
            result.onSuccess { logger.info("Undeployment successful for match '$matchId'") }
            result.onFailure { logger.error("Failed to undeploy match verticle for '$matchId'") }
        }
    }
}