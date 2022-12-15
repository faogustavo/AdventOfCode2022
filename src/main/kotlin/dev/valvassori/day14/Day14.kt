package dev.valvassori.day14

import dev.valvassori.max
import dev.valvassori.min
import dev.valvassori.readInput
import dev.valvassori.unorderedRange
import java.util.PrimitiveIterator

fun main() {
    val chart = SandChart()
    val input = readInput("day14")
        .map { it.split(" -> ").windowed(2) }
        .flatten()

    input.forEach { (from, to) ->
        chart.drawRock(from.asCoordinates, to.asCoordinates)
    }

    var count: Long = 0

    while(chart.produce()) {
        count += 1
    }

    chart.render(hasGround = false)
    println()
    println("Step 1 - Units: $count")

    while(chart.produce(hasGround = true)) {
        count += 1
    }

    println()
    chart.render(hasGround = true)
    println()
    println("Step 2 - Units: $count")
}

private val String.asCoordinates: SandChart.Coordinates
    get() {
        val (x, y) = split(",")
        return SandChart.Coordinates(
            x.toInt(),
            y.toInt(),
        )
    }

private class SandChart(
    val sandOutputPos: Coordinates = Coordinates(500, 0)
) {
    enum class Movement { NONE, STRAIGHT, DOWN_AND_LEFT, DOWN_AND_RIGHT }
    data class Coordinates(val x: Int, val y: Int) {
        fun oneDown() = copy(y = y + 1)
        fun oneDownAndLeft() = copy(x = x - 1, y = y + 1)
        fun oneDownAndRight() = copy(x = x + 1, y = y + 1)
        fun move(movement: Movement) = copy(
            x = when (movement) {
                Movement.DOWN_AND_LEFT -> x - 1
                Movement.DOWN_AND_RIGHT -> x + 1
                else -> x
            },
            y = when (movement) {
                Movement.NONE -> y
                else -> y + 1
            },
        )
    }

    private val map = mutableMapOf<Coordinates, String>()

    var minX: Int = Int.MAX_VALUE
        private set
    var maxX: Int = Int.MIN_VALUE
        private set
    var maxY: Int = Int.MIN_VALUE
        private set(value) {
            field = value
            ground = value + 2
        }
    var ground: Int = Int.MIN_VALUE
        private set

    fun drawRock(from: Coordinates, to: Coordinates) {
        minX = min(minX, from.x, to.x)
        maxX = max(maxX, from.x, to.x)
        maxY = max(maxY, from.y, to.y)

        if (from.x == to.x) {
            // Vertical Line
            val x = from.x
            val eachY = from.y unorderedRange to.y
            for (y in eachY) {
                map[Coordinates(x, y)] = "#"
            }
        }

        if (from.y == to.y) {
            // Vertical Line
            val y = from.y
            val eachX = from.x unorderedRange to.x
            for (x in eachX) {
                map[Coordinates(x, y)] = "#"
            }
        }
    }

    fun render(hasGround: Boolean) {
        val lastLayer = (if (hasGround) ground else maxY)

        val xRange = minX..maxX
        val yRange = 0..lastLayer
        for (y in yRange) {
            for (x in xRange) {
                val coordinates = Coordinates(x, y)
                print(
                    when {
                        map.containsKey(coordinates) -> map[coordinates]
                        coordinates == sandOutputPos -> "+"
                        y == lastLayer && hasGround -> "#"
                        else -> "."
                    }
                )
            }
            println()
        }
    }

    fun produce(
        hasGround: Boolean = false,
    ): Boolean {
        if (map.containsKey(sandOutputPos)) return false

        var position = sandOutputPos
        var movement = produceMovementFor(position, hasGround)

        val lastValidYPos = if (hasGround) ground - 1 else maxY
        while(movement != Movement.NONE && position.y < lastValidYPos) {
            position = position.move(movement)
            movement = produceMovementFor(position, hasGround)

            minX = min(minX, position.x)
            maxX = max(maxX, position.x)
        }

        val thresholdToFail = if (hasGround) ground else maxY
        if (position.y >= thresholdToFail) {
            return false
        }

        map[position] = "o"
        return true
    }

    private fun produceMovementFor(
        coordinates: Coordinates,
        hasGround: Boolean,
    ): Movement =
        when {
            coordinates.y == ground && hasGround -> Movement.NONE
            !map.containsKey(coordinates.oneDown()) -> Movement.STRAIGHT
            !map.containsKey(coordinates.oneDownAndLeft()) -> Movement.DOWN_AND_LEFT
            !map.containsKey(coordinates.oneDownAndRight()) -> Movement.DOWN_AND_RIGHT
            else -> Movement.NONE
        }
}
