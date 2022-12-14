package dev.valvassori.day12

import dev.valvassori.readInput
import kotlin.math.min

fun main() {
    val input: List<List<Char>> = readInput("day12")
        .map { it.toCharArray().toList() }

    // Convert into nodes
    val mappedMatrix = Array(input.size) { row ->
        Array(input[row].size) { column ->
            val height = input[row][column]
            GraphTreeNode(height)
        }
    }

    // Link node with neighbors
    mappedMatrix.forEachIndexed { row, rowNodes ->
        rowNodes.forEachIndexed { column, node ->
            if (row > 0) {
                node.link(mappedMatrix[row - 1][column])
            }

            if (row < mappedMatrix.lastIndex) {
                node.link(mappedMatrix[row + 1][column])
            }

            if (column > 0) {
                node.link(rowNodes[column - 1])
            }

            if (column < rowNodes.lastIndex) {
                node.link(rowNodes[column + 1])
            }
        }
    }

    // Find distance from start to end
    val root = mappedMatrix.flatten().find { it.isStart }
        ?: error("Start not found")

    root.routes.forEach { it.updateWithNeighborDistance(1) }
    val stack = ArrayDeque(root.routes)
    while(stack.isNotEmpty()) {
        val node = stack.removeFirst()
        node.routes.forEach { route ->
            route.updateWithNeighborDistance(node.distanceFromStart + 1)
            if (route.routes.any { it.distanceFromStart < 0 } && !stack.contains(route)) {
                stack.add(route)
            }
        }
    }

    // Find distance in reverse way
    val destination = mappedMatrix.flatten().find { it.isDestination }
        ?: error("Start not found")

    destination.reverseRoutes.forEach { it.updateWithReverseNeighborDistance(1) }
    stack.addAll(destination.reverseRoutes)
    while(stack.isNotEmpty()) {
        val node = stack.removeFirst()
        node.reverseRoutes.forEach { route ->
            route.updateWithReverseNeighborDistance(node.distanceFromDestination + 1)
            if (route.reverseRoutes.any { it.distanceFromDestination < 0 } && !stack.contains(route)) {
                stack.add(route)
            }
        }
    }

    // Step 1
    println(root)
    println(destination)

    // Step 2
    println(
        mappedMatrix.flatten().filter { it.char == 'a' && it.distanceFromDestination > 0 }
            .minBy { it.distanceFromDestination }
    )
}

private val CHAR_INDEX = ('a'..'z').toMutableList()
    .apply {
        add(0, 'S')
        add('E')
    }
private val Char.asChallengeHeight: Int
    get() = CHAR_INDEX.indexOf(this)

class GraphTreeNode(val char: Char) {
    val height: Int = char.asChallengeHeight
    val isStart = height == 0
    val isDestination = height == CHAR_INDEX.size - 1

    val routes = mutableListOf<GraphTreeNode>()
    val reverseRoutes = mutableListOf<GraphTreeNode>()
    var distanceFromStart: Int = if (isStart) 0 else -1
    var distanceFromDestination: Int = if (isDestination) 0 else -1

    fun link(other: GraphTreeNode) {
        val distance = height - other.height
        if (distance >= -1) {
            routes += other

            if (!other.reverseRoutes.contains(this)) {
                other.reverseRoutes += this
            }
        }
    }

    fun updateWithNeighborDistance(neighborDistance: Int) {
        distanceFromStart = if (distanceFromStart < 0) {
            neighborDistance
        } else {
            min(neighborDistance, distanceFromStart)
        }
    }

    fun updateWithReverseNeighborDistance(neighborDistance: Int) {
        distanceFromDestination = if (distanceFromDestination < 0) {
            neighborDistance
        } else {
            min(neighborDistance, distanceFromDestination)
        }
    }

    override fun hashCode(): Int {
        return height.hashCode() + routes.hashCode()
    }

    override fun toString(): String =
        "Node(elevation= ${CHAR_INDEX[height]}, distance= $distanceFromStart, distanceFromDestination= $distanceFromDestination)"

    fun shortString(): String =
        "${CHAR_INDEX[height]}(%3d)".format(distanceFromStart)
}
