package com.ivantodor.snake.arena.server.verticles

import com.ivantodor.snake.arena.common.response.ServerRejectResponse
import com.ivantodor.snake.arena.server.helper.Globals
import com.ivantodor.snake.arena.server.helper.onFailure
import com.ivantodor.snake.arena.server.helper.onSuccess
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.ServerWebSocket
import io.vertx.core.json.Json
import mu.KLogging

/**
 * @author Ivan Todorovic
 */

class HttpVerticle(val port: Int) : AbstractVerticle() {
    companion object : KLogging() {
        // todo: Switch to cluster-wide maps
        private val clientNameRegex = """\/snakearena\/(.+)""".toRegex()
    }

    override fun start() {
        vertx.createHttpServer().websocketHandler { ws ->
            logger.info("Connection: ${ws.localAddress()} - ${ws.textHandlerID()}")

            if (ws.path().contains("snakearena/")) {
                setupClientEnvironment(ws)
            } else {
                logger.info("Connection rejected")
                ws.reject()
            }
        }.listen(port) { result ->
            result.onSuccess { logger.info("Http server up and running") }
            result.onFailure { logger.error("Http server binding failed") }
        }
    }

    private fun setupClientEnvironment(websocket: ServerWebSocket) {
        val nameMatching = clientNameRegex.find(websocket.path())
        //todo checking
        val name = nameMatching?.groups?.get(1)?.value ?: "null"

        if (Globals.clientList.contains(name)) {
            logger.debug("Name $name already taken")
            val rejectionResponse = Json.encode(ServerRejectResponse("Name already taken"))
            websocket.writeFinalTextFrame(rejectionResponse)
            websocket.close()
        } else {
            Globals.clientList.add(name)
            vertx.deployVerticle(ClientVerticle(name, websocket)) { deploymentResult ->
                deploymentResult.onSuccess { logger.info("Client verticle '$name' deployed") }
                deploymentResult.onFailure { logger.error("Client '$name' verticle deployment failed") }
            }
        }
    }
}

