package com.ivantodor.snake.arena.server.helper

import com.ivantodor.snake.arena.common.model.Direction
import io.vertx.core.AsyncResult

/**
 * @author Ivan Todorovic
 */

/**
 * Extension for Direction model inside common module
 */
fun Direction.angle(): Double {
    when (this) {
        Direction.FORWARD -> return 0.0
        Direction.LEFT -> return Math.PI / 2
        Direction.RIGHT -> return -Math.PI / 2
        else -> return 0.0
    }
}

fun <T> AsyncResult<T>.onSuccess(f: (T) -> Unit): Unit {
    if(succeeded()) {
        f(result())
    }
}

fun <T> AsyncResult<T>.onFailure(f: () -> Unit): Unit {
    if(!succeeded()) {
        f()
    }
}