package dev.valvassori.day09

import dev.valvassori.readInput
import kotlin.math.abs

fun main() {
    val input = readInput("day09").map(::parseLine)
    run(1, input)
    run(9, input)
}

private fun run(knotCount: Int, input: List<Pair<Char, Int>>) {
    val start = Coordinates(0, 0)
    var head = start

    val knots = Array(knotCount) { start }
    val allKnotsPositions = Array(knotCount) { mutableSetOf(start) }

    fun moveKnot(knotIndex: Int) {
        val knotHead = if (knotIndex == 0) head else knots[knotIndex - 1]
        knots[knotIndex] = knots[knotIndex].moveToNearestPositionOf(knotHead)
        allKnotsPositions[knotIndex].add(knots[knotIndex])
    }

    input.forEach { (direction, steps) ->
        repeat(steps) {
            head = head.move(direction)
            repeat(knotCount, ::moveKnot)
        }
    }

    println(allKnotsPositions.last().size)
}

private data class Coordinates(
    val x: Int,
    val y: Int,
) {
    fun isTouching(other: Coordinates) =
        abs(verticalDistanceTo(other)) <= 1 && abs(horizontalDistanceTo(other)) <= 1

    fun verticalDistanceTo(other: Coordinates) = other.y - y
    fun horizontalDistanceTo(other: Coordinates) = other.x - x

    fun move(direction: Char) = when (direction) {
        'U' -> copy(y = y + 1)
        'R' -> copy(x = x + 1)
        'D' -> copy(y = y - 1)
        'L' -> copy(x = x - 1)
        else -> error("Invalid direction '$direction'")
    }

    fun moveToNearestPositionOf(head: Coordinates): Coordinates {
        if (head.isTouching(this)) return this

        val verticalDistance = verticalDistanceTo(head)
        val horizontalDistance = horizontalDistanceTo(head)

        // two steps directly up, down, left, or right
        // move one step in that direction
        if (abs(verticalDistance) == 2 && horizontalDistance == 0)
            return copy(y = y + verticalDistance.limitToOne)

        if (abs(horizontalDistance) == 2 && verticalDistance == 0)
            return copy(x = x + horizontalDistance.limitToOne)

        return copy(
            x = x + horizontalDistance.limitToOne,
            y = y + verticalDistance.limitToOne,
        )
    }
}

private val Int.limitToOne: Int
    get() = coerceAtLeast(-1).coerceAtMost(1)

private fun parseLine(line: String): Pair<Char, Int> =
    line.first() to line.split(" ").last().toInt()
