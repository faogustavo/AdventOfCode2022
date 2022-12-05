package dev.valvassori.day05

import dev.valvassori.readInput

fun main() {
    readData().runAndPrintResult(::stepOne)
    readData().runAndPrintResult(::stepTwo)
}

private fun stepOne(stacks: Map<String, ArrayDeque<String>>, command: ChallengeData.Command) {
    val from = requireNotNull(stacks[command.from])
    val to = requireNotNull(stacks[command.to])

    repeat(command.amount) {
        to.add(from.removeLast())
    }
}

private fun stepTwo(stacks: Map<String, ArrayDeque<String>>, command: ChallengeData.Command) {
    fun <T> ArrayDeque<T>.removeLast(n: Int): ArrayDeque<T> {
        val result = ArrayDeque<T>()

        repeat(n) {
            result.add(removeLast())
        }

        result.reverse()
        return result
    }

    val from = requireNotNull(stacks[command.from])
    val to = requireNotNull(stacks[command.to])

    to.addAll(from.removeLast(command.amount))
}

private data class ChallengeData(
    val stacks: Map<String, ArrayDeque<String>>,
    val commands: List<Command>,
) {
    data class Command(
        val amount: Int,
        val from: String,
        val to: String,
    )

    // Part One
    fun runAndPrintResult(runner: (Map<String, ArrayDeque<String>>, Command) -> Unit) {
        // Run
        commands.forEach { runner(stacks, it) }

        // Print
        for (iteration in 1 .. stacks.size) {
            print(stacks[iteration.toString()]?.last()?.get(1))
        }

        println()
    }
}


private fun readData(): ChallengeData {
    val fileData = readInput("day05")
    val idx = fileData.indexOfFirst { it.isBlank() }

    val totalColumns = fileData[idx - 1].trim().split(" ").last().toInt()

    val table = fileData.subList(0, idx - 1)
    val stacks = mutableMapOf<String, ArrayDeque<String>>()
    for (iteration in 1 .. totalColumns) {
        val iterationStack = ArrayDeque<String>()
        table.forEach { row ->
            val item = row.substring(iteration.idxForColumnNumber)
            if (item.isNotBlank()) {
                iterationStack.add(item)
            }
        }

        // Reverse stack as we are reading from top to bottom
        iterationStack.reverse()
        stacks["$iteration"] = iterationStack
    }

    val commands = fileData.subList(idx + 1, fileData.size).map { line ->
        val parts = line.split(" ")
        ChallengeData.Command(parts[1].toInt(), parts [3], parts[5])
    }

    return ChallengeData(stacks, commands)
}

private val Int.idxForColumnNumber: IntRange
    get() {
        val endIdx = (this * 3) + (this - 1).coerceAtLeast(0)
        return (endIdx - 3) until endIdx
    }
