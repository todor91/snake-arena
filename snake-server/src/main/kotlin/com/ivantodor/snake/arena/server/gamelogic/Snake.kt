package com.ivantodor.snake.arena.server.gamelogic

import com.ivantodor.snake.arena.common.model.Direction
import com.ivantodor.snake.arena.server.helper.angle
import java.awt.Point
import java.util.*

/**
 * @author Ivan Todorovic
 */
class Snake(val pointList: LinkedList<Point> = LinkedList<Point>()) {

    fun appendTail(): Int {
        val lastTwo = listOf(pointList.last, pointList.get(pointList.size - 2))
        val directionX = Integer.signum(lastTwo[1].x - lastTwo[0].x)
        val directionY = Integer.signum(lastTwo[1].y - lastTwo[0].y)

        val tail = getTail()
        pointList.add(Point(tail.x + directionX, tail.y + directionY))

        return pointList.size
    }

    fun selfCollision(): Boolean {
        val pointSet = mutableSetOf<Point>()

        pointList.forEach { elem ->
            if(!pointSet.add(elem))
                return true
        }
        return false
    }

    //todo check if there is only one element
    fun move(direction: Direction): Point {
        val angle = direction.angle()
        val head = pointList.first
        val next = pointList[1]

        val directionX = Integer.signum(head.x - next.x)
        val directionY = Integer.signum(head.y - next.y)

        val sin = Math.sin(-angle) // minus because y is top -> down
        val cos = Math.cos(angle)

        val newX = directionX * cos - directionY * sin
        val newY = directionX * sin + directionY * cos

        pointList.addFirst(Point(head.x + newX.toInt(), head.y + newY.toInt()))
        pointList.removeLast()
        return pointList.first
    }

    fun getHead(): Point {
        return pointList.first
    }

    fun getTail(): Point {
        return pointList.last
    }
}