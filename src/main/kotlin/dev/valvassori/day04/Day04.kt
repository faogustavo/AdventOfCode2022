package dev.valvassori.day04

import dev.valvassori.readInput

fun main() {
    val input: List<Pair<IntRange, IntRange>> = readInput("day04")
        .map { line ->
            val (first, second) = line.split(",").map {
                val (first, second) = it.split("-")
                first.toInt() .. second.toInt()
            }
            first to second
        }

    // Part one
    operator fun IntRange.contains(other: IntRange): Boolean =
        other.first in this && other.last in this

    println(
        input.count { (first, second) ->
            first in second || second in first
        }
    )

    // Part two
    infix fun IntRange.overlap(other: IntRange): Boolean =
        other.first in this || other.last in this || first in other || last in other

    println(
        input.count { (first, second) -> first overlap second }
    )
}
