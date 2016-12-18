package com.ivantodor.snake.arena.server.verticles

import com.ivantodor.snake.arena.common.model.MatchConstraints
import com.ivantodor.snake.arena.common.request.MatchInvitationRequest
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import mu.KLogging

/**
 * @author Ivan Todorovic
 */

class MatchOrganizerVerticle : AbstractVerticle() {
    companion object : KLogging()

    /**
     * Map of pending invitations
     * key - invitationId
     * value - list of (initiator - invited players)
     */
    private val pendingInvitations: MutableMap<String, InvitationStatus> = mutableMapOf()

    override fun start() {

        // Listen for incoming invitation requests
        vertx.eventBus().consumer<JsonObject>(Address.MatchOrganizer.Invite) { msg ->
            val request = Json.decodeValue(msg.body().toString(), MatchInvitationRequest::class.java)
            val initiator = msg.headers()["clientId"]

            val invitationStatus = InvitationStatus(initiator, mutableMapOf(), request.matchConstraints)
            invitationStatus.responses.putAll(request.invitedPlayers.map { Pair(it, false) }.toMap())

            pendingInvitations.put(request.invitationId, invitationStatus)

            request.invitedPlayers.forEach { player ->
                logger.debug("Sending invitation to $player")
                val clientAddress = Address.Client.clientHanlder(player)
                vertx.eventBus().send(clientAddress, msg.body())
            }
        }

        // Process invitation responses
        vertx.eventBus().consumer<JsonObject>(Address.MatchOrganizer.Respond) { msg ->
            logger.debug("MatchOrganizer received response: ${msg.body()}")
            val invitationId = msg.body().getString("invitationId")
            val clientId = msg.headers()["clientId"]
            val response = msg.body().getBoolean("response")

            pendingInvitations[invitationId]?.responses?.put(clientId, response)

            //if all players accepted invitation
            if(pendingInvitations[invitationId]?.responses?.all { it.value == true } ?: false) {
                //deploy game verticle
                logger.info("Game verticle for invitation '$invitationId' deployed")
                val lists = mutableListOf<String>()

                // todo fix this
                lists.add(pendingInvitations[invitationId]?.initiator ?: "123")
                lists.addAll(pendingInvitations[invitationId]?.responses?.keys ?: listOf())

                val matchVerticleOptions = DeploymentOptions().setWorker(true)
                vertx.deployVerticle(MatchVerticle(invitationId, lists, pendingInvitations[invitationId]?.matchConstraints ?: MatchConstraints()), matchVerticleOptions)
                pendingInvitations.remove(invitationId)
            }
        }
    }
}

data class InvitationStatus(val initiator: String, val responses: MutableMap<String, Boolean>, val matchConstraints: MatchConstraints)