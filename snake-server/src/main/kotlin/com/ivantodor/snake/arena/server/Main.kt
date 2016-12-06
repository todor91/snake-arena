package com.ivantodor.snake.arena.server

import com.ivantodor.snake.arena.server.verticles.HttpVerticle
import com.ivantodor.snake.arena.server.verticles.MatchOrganizerVerticle
import com.ivantodor.snake.arena.server.verticles.StupidAiVerticle
import io.vertx.core.Vertx

fun main(args: Array<String>) {
    val vertx = Vertx.vertx()

    vertx.deployVerticle(HttpVerticle(8080))

    vertx.deployVerticle(StupidAiVerticle("STUPID-AI-1"))
    vertx.deployVerticle(StupidAiVerticle("STUPID-AI-2"))
    vertx.deployVerticle(StupidAiVerticle("STUPID-AI-3"))

    vertx.deployVerticle(MatchOrganizerVerticle())

}