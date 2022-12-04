package dev.valvassori.day03

import dev.valvassori.readInput

private val PRIORITY = (('a' .. 'z') + ('A' .. 'Z'))
    .mapIndexed { index, c -> c to (index + 1) }
    .toMap()

fun main() {
    val data = readInput("day03")

    // Step 1
    val count = data.sumOf { line ->
        // Find middle
        val middle = line.length.ushr(1)

        // Get first stack
        val left = line.substring(0 until middle).toSet()

        // Get second stack
        val right = line.substring(middle until line.length).toSet()

        // Find intersection and sum by priorities
        left.intersect(right).sumOf { PRIORITY[it] ?: 0 }
    }

    println(count)

    // Step 2
    val count2 = data.chunked(3)
        .sumOf { it.reduce().sumOf { PRIORITY[it] ?: 0 } }

    println(count2)
}

private fun List<String>.reduce(): CharArray = map { it.toCharArray() }
    .reduce { acc, line -> acc.intersect(line.toSet()).toCharArray() }
