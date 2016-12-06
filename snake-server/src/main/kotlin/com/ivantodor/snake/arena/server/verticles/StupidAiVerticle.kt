package com.ivantodor.snake.arena.server.verticles

import com.ivantodor.snake.arena.common.MoveAction
import com.ivantodor.snake.arena.common.model.Direction
import com.ivantodor.snake.arena.common.request.MatchInvitationRequest
import com.ivantodor.snake.arena.common.response.MatchInvitationResponse
import com.ivantodor.snake.arena.common.response.MatchStatusResponse
import com.ivantodor.snake.arena.server.helper.Globals
import io.vertx.core.AbstractVerticle
import io.vertx.core.eventbus.DeliveryOptions
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import mu.KLogging

/**
 * @author Ivan Todorovic
 */

class StupidAiVerticle(val clientId: String) : AbstractVerticle() {
    companion object : KLogging()

    override fun start() {
        Globals.clientList.add(clientId)
        val clientAddress = Address.Client.clientHanlder(clientId)

        vertx.eventBus().consumer<JsonObject>(clientAddress) { jsonMsg ->
            val msg = jsonMsg.body()

            val type = msg.getString("type")

            when(type) {
                MatchInvitationRequest.TYPE -> acceptMatchInvitation(Json.decodeValue(msg.toString(), MatchInvitationRequest::class.java))
                MatchStatusResponse.TYPE -> moveSnake(Json.decodeValue(msg.toString(), MatchStatusResponse::class.java))
            }
        }
    }

    private fun acceptMatchInvitation(invitation: MatchInvitationRequest) {
        val deliveryOptions = DeliveryOptions()
        deliveryOptions.addHeader("clientId", clientId)
        val response = MatchInvitationResponse(invitation.invitationId, true)
        vertx.eventBus().send(Address.MatchOrganizer.Respond, JsonObject(Json.encode(response)), deliveryOptions)
        logger.debug("Stupid AI accepted invitation")
    }

    private fun moveSnake(status: MatchStatusResponse) {
        val deltaX = status.foodPosition.x - (status.snakes[clientId]?.get(0)?.x ?: 0)
        val deltaY = status.foodPosition.y - (status.snakes[clientId]?.get(0)?.y ?: 0)

        val dirX = (status.snakes[clientId]?.get(0)?.x ?: 0) - (status.snakes[clientId]?.get(1)?.x ?: 0)
        val dirY = (status.snakes[clientId]?.get(0)?.y ?: 0) - (status.snakes[clientId]?.get(1)?.y ?: 0)


        var tempAction = Direction.FORWARD
        if(deltaX != 0) {
            if(Integer.signum(dirX) != Integer.signum(deltaX))
                tempAction = Direction.RIGHT
        } else {
            if (deltaY > 0 && dirX > 0)
                tempAction = Direction.RIGHT
            else if (deltaY < 0 && dirX > 0)
                tempAction = Direction.LEFT
            else if (deltaY < 0 && dirX < 0)
                tempAction = Direction.RIGHT
            else if (deltaY > 0 && dirX < 0)
                tempAction = Direction.LEFT
        }

        val action = MoveAction(status.matchId, tempAction)

        val deliveryOptions = DeliveryOptions()
        deliveryOptions.addHeader("clientId", clientId)


        vertx.eventBus().send(Address.Match.action(status.matchId), JsonObject(Json.encode(action)), deliveryOptions)
    }
}