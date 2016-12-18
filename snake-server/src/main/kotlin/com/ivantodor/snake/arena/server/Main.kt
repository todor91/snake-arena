package com.ivantodor.snake.arena.server

import com.ivantodor.snake.arena.server.verticles.HttpVerticle
import com.ivantodor.snake.arena.server.verticles.MatchOrganizerVerticle
import com.ivantodor.snake.arena.server.verticles.StupidAiVerticle
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import mu.KLogging

fun main(args: Array<String>) {
    val vertxOptions = VertxOptions().setWorkerPoolSize(128)
    val vertx = Vertx.vertx(vertxOptions)

    KLogging().logger.info("Starting Http server...")
    vertx.deployVerticle(HttpVerticle(8080))

    vertx.deployVerticle(StupidAiVerticle("STUPID-AI-1"))
    vertx.deployVerticle(StupidAiVerticle("STUPID-AI-2"))
    vertx.deployVerticle(StupidAiVerticle("STUPID-AI-3"))

    vertx.deployVerticle(MatchOrganizerVerticle())

}