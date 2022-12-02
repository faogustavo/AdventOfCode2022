package dev.valvassori.day01

import dev.valvassori.readInput
import dev.valvassori.toListOfIntList

fun main() {
    val data = readInputGrouped()
    val caloriesByEachElves = data.toListOfIntList()
        .map { it.sum() }
        .sorted()

    // Part 1
    // As list is already sorted, just get last value
    println(caloriesByEachElves.last())

    // Part 2
    // As list is already sorted, just get last 3 values and sum
    println(caloriesByEachElves.takeLast(3).sum())
}

private fun readInputGrouped(): List<List<String>> {
    val result = mutableListOf<List<String>>()

    val fileContent = readInput("day01")
    val iteration = mutableListOf<String>()

    for (line in fileContent) {
        if (line.isBlank()) {
            if (iteration.isNotEmpty()) {
                result.add(iteration.toList())
                iteration.clear()
            }
        } else {
            iteration.add(line)
        }
    }

    if (iteration.isNotEmpty()) {
        result.add(iteration)
    }

    return result
}
