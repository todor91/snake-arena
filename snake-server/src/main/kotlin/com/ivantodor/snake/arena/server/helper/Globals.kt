package com.ivantodor.snake.arena.server.helper

import io.vertx.core.impl.ConcurrentHashSet

/**
 * @author Ivan Todorovic
 */

//todo without this

object Globals {
    val clientList = ConcurrentHashSet<String>()
}