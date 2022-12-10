package dev.valvassori.day10

import dev.valvassori.readInput

private val CHECK_RANGE = (60 .. 1_000_000 step 40).toList()
fun main() {
    val input = readInput("day10")
        .map(Command::of)
        .toMutableList()

    val signalHistory = mutableListOf<Pair<Int, Int>>()
    val ctrScreen = Array(6) {
        Array(40) { ' ' }
    }

    var currentCycle = 0
    var registerX = 1
    var spritePosition: IntRange = 0..2

    var currentCommand: Command? = null
    var remainingCycles = 0

    while(input.isNotEmpty() || currentCommand != null) {
        if (remainingCycles == 0) {
            when (currentCommand) {
                is Command.Add -> {
                    registerX += currentCommand.value
                    spritePosition = (registerX - 1) .. (registerX + 1)
                }

                Command.Noop -> {
                    // Do Nothing
                }
                null -> {
                    // Do Nothing
                }
            }
        }

        if (currentCommand == null || remainingCycles == 0) {
            currentCommand = input.removeFirstOrNull()
            remainingCycles = currentCommand?.cycles ?: 0
        }

        val humanCycleNumber = currentCycle + 1
        if (humanCycleNumber == 20 || humanCycleNumber in CHECK_RANGE) {
            signalHistory.add(humanCycleNumber to registerX)
        }

        // Render
        val row: Int = currentCycle / 40
        val column = (currentCycle % 40)
        if (row < 6)
            ctrScreen[row][column] = if (column in spritePosition) '#' else '.'

        currentCycle += 1
        remainingCycles -= 1
    }

    println(
        signalHistory.take(6)
            .sumOf { (left, right) -> left * right }
    )

    ctrScreen.forEach { line ->
        println(line.joinToString(""))
    }
}

private sealed class Command {
    abstract val cycles: Int

    object Noop : Command() {
        override val cycles: Int = 1
    }

    data class Add(val value: Int) : Command() {
        override val cycles: Int = 2
    }

    companion object {
        fun of(line: String): Command {
            val parts = line.split(" ")
            return when (parts.first()) {
                "noop" -> Noop
                "addx" -> Add(parts.last().toInt())
                else -> error("Invalid command '${parts.first()}'")
            }
        }
    }
}
