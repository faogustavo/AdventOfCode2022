package dev.valvassori.day02

import dev.valvassori.readInput

/**
 * A/X = Rock
 * B/Y = Paper
 * C/Z = Scissors
 */
fun main() {
    val input = readInput("day02")
        .map { it.first() to it.last() }

    // Part 1
    println(input.sumOf { it.pointsForThisMatch })

    // Part 2
    println(input.sumOf { it.pointsForThisMatchWithStrategy })
}

private val Pair<Char, Char>.pointsForThisMatch: Int
    get() {
        var acc = second.pointsForOption

        if (first.moveToWin == second)
            acc += 6

        if (first.moveToDraw == second)
            acc += 3

        return acc
    }

private val Pair<Char, Char>.pointsForThisMatchWithStrategy: Int
    get() {
        if (second == 'X')
            return first.moveToLose.pointsForOption

        if (second == 'Y')
            return 3 + first.moveToDraw.pointsForOption

        if (second == 'Z')
            return 6 + first.moveToWin.pointsForOption

        return 0
    }

private val Char.pointsForOption: Int
    get() = when (this) {
        'Z' -> 3
        'Y' -> 2
        'X' -> 1
        else -> error("Invalid move")
    }

private val Char.moveToWin: Char
    get() = when (this) {
        'A' -> 'Y'
        'B' -> 'Z'
        'C' -> 'X'
        else -> error("Invalid move")
    }

private val Char.moveToDraw: Char
    get() = when (this) {
        'A' -> 'X'
        'B' -> 'Y'
        'C' -> 'Z'
        else -> error("Invalid move")
    }

private val Char.moveToLose: Char
    get() = when (this) {
        'A' -> 'Z'
        'B' -> 'X'
        'C' -> 'Y'
        else -> error("Invalid move")
    }
