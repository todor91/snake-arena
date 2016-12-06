package com.ivantodor.snake.arena.server.verticles

import com.ivantodor.snake.arena.common.MoveAction
import com.ivantodor.snake.arena.common.request.MatchDiscoverRequest
import com.ivantodor.snake.arena.common.request.MatchInvitationRequest
import com.ivantodor.snake.arena.common.request.PlayerListRequest
import com.ivantodor.snake.arena.common.response.MatchInvitationResponse
import com.ivantodor.snake.arena.common.response.PlayerListResponse
import com.ivantodor.snake.arena.server.helper.Globals
import com.ivantodor.snake.arena.server.helper.onFailure
import com.ivantodor.snake.arena.server.helper.onSuccess
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import mu.KLogging

/**
 * @author Ivan Todorovic
 */

class ClientVerticle(val clientId: String, val websocket: ServerWebSocket) : AbstractVerticle() {
    companion object : KLogging()

    override fun start() {
        val clientAddress = Address.Client.clientHanlder(clientId)

        registerWebsocketCloseHandler()

        registerWebsocketInputHandler()

        vertx.eventBus().consumer<JsonObject>(clientAddress) { jsonMsg ->
            val msg = jsonMsg.body()

            websocket.writeFinalTextFrame(msg.toString())
        }
    }

    private fun broadcastDiscoverMatchesRequest() {
        val internalDiscoverRequest = JsonObject().put("clientId", clientId)
        vertx.eventBus().publish(Address.Match.Discover, internalDiscoverRequest)
    }

    private fun respondWithPlayerList() {
        val responseList = PlayerListResponse(Globals.clientList.toList())
        websocket.writeFinalTextFrame(Json.encode(responseList))
    }

    private fun sendMatchInvitation(invitationRequest: JsonObject) {
        val deliveryOptions = DeliveryOptions()
        deliveryOptions.addHeader("clientId", clientId)
        vertx.eventBus().send(Address.MatchOrganizer.Invite, invitationRequest, deliveryOptions)
        logger.debug("Invitation ${invitationRequest.getString("matchId")} sent to match organizer")
    }

    private fun sendMatchInvitationResponse(invitationResponse: JsonObject) {
        val deliveryOptions = DeliveryOptions()
        deliveryOptions.addHeader("clientId", clientId)
        vertx.eventBus().send(Address.MatchOrganizer.Respond, invitationResponse, deliveryOptions)
        logger.debug("Response ${invitationResponse.getString("matchId")} sent to match organizer")
    }

    private fun sendClientMoveAction(moveActionJson: JsonObject) {
        val moveAction = Json.decodeValue(moveActionJson.toString(), MoveAction::class.java)
        val deliveryOptions = DeliveryOptions()
        deliveryOptions.addHeader("clientId", clientId)

        vertx.eventBus().send(Address.Match.action(moveAction.matchId), moveActionJson, deliveryOptions)
    }

    private fun registerWebsocketCloseHandler() {
        websocket.closeHandler {
            logger.info("Undeploying client verticle for '$clientId'")
            vertx.undeploy(deploymentID()) { result ->
                result.onSuccess {
                    logger.info("Undeployment successful for '$clientId'")
                    //todo cluster-wide maps !!!
                    Globals.clientList.remove(clientId)
                }
                result.onFailure { logger.error("Failed to undeploy client verticle for '$clientId'") }
            }
        }
    }

    /**
     * Processes messages sent by client
     */
    private fun registerWebsocketInputHandler() {
        websocket.handler { buffer ->
            logger.debug("Received message from client '$clientId': $buffer")

            val messageType = buffer.toJsonObject().getString("type")
            when (messageType) {
                MatchDiscoverRequest.TYPE -> broadcastDiscoverMatchesRequest()
                PlayerListRequest.TYPE -> respondWithPlayerList()
                MatchInvitationRequest.TYPE -> sendMatchInvitation(buffer.toJsonObject())
                MatchInvitationResponse.TYPE -> sendMatchInvitationResponse(buffer.toJsonObject())
                MoveAction.TYPE -> sendClientMoveAction(buffer.toJsonObject())
                else -> logger.error("Invalid message type: $messageType")
            }
        }
    }
}