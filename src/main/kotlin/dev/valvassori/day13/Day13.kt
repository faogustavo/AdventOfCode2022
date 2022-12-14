package dev.valvassori.day13

import dev.valvassori.*

fun main() {
    val input = readInput("day13")
        .chunked(3)
        .map {
            val (first, second) = it
            first.parseInputLine() to second.parseInputLine()
        }

    // Part One
    printFun = noop
    val comparisons = input.mapIndexed { index, pair ->
        tabbedPrintln("== Pair ${index + 1} ==", 0)
        (if (pair.first.deepCompare(pair.second, 0) <= 0) index + 1 else 0).also { tabbedPrintln() }
    }
    println(comparisons)
    println(comparisons.sum())

    // Part 2
    val dividerPackages: List<List<Any>> = listOf("[[2]]".parseInputLine(), "[[6]]".parseInputLine())
    val inputAsList: List<List<Any>> = input.flatMap { listOf(it.first, it.second) }
    val sortedPackets = (inputAsList + dividerPackages)
        .sortedWith { left, right -> left.deepCompare(right, 1) }

    println(
        dividerPackages.map { sortedPackets.indexOf(it) + 1 }.reduce(Int::times)
    )
}

private fun List<Any?>.deepCompare(other: List<Any?>, level: Int): Int {
    tabbedPrintln("- Compare $this vs $other", level)
    val allIndices = indices intersect other.indices

    for (idx in allIndices) {
        val result = this[idx].compareTo(other[idx], level + 1)
        if (result < 0) return -1
        if (result > 0) return 1
    }

    if (lastIndex < other.lastIndex) {
        tabbedPrintln("- Left side ran out of items, so inputs are in the right order", level + 1)
        return -1
    }

    if (lastIndex > other.lastIndex) {
        tabbedPrintln("- Right side ran out of items, so inputs are NOT in the right order", level + 1)
        return 1
    }

    return 0
}

private fun Any?.compareTo(other: Any?, level: Int): Int {
    if (this is Int && other is Int) {
        tabbedPrintln("- Compare $this vs $other", level)
        if (this < other) tabbedPrintln("Left side is smaller, so inputs are in the right order", level + 1)
        if (this > other) tabbedPrintln("Right side is smaller, so inputs are NOT in the right order", level + 1)
        return this.compareTo(other)
    }

    if (this is List<*> && other is List<*>) {
        return this.deepCompare(other, level)
    }

    if (this is Int && other is List<*>) {
        if (other.isEmpty()) {
            tabbedPrintln("- Right side ran out of items, so inputs are NOT in the right order", level + 1)
            return 1
        }
        return listOf(this).deepCompare(other, level)
    }

    if (this is List<*> && other is Int) {
        if (isEmpty()) {
            tabbedPrintln("- Left side ran out of items, so inputs are in the right order", level + 1)
            return -1
        }
        return this.deepCompare(listOf(other), level + 1)
    }

    return 0
}

private fun String.parseInputLine(): List<Any> {
    val result: MutableList<Any> = mutableListOf()
    val stack = ArrayDeque<MutableList<Any>>(listOf(result))
    var acc: String = ""

    toCharArray().forEach { char ->
        if (char.isDigit()) {
            acc += char
        }

        if (char == ',') {
            if (acc.isNotEmpty()) {
                stack.last().add(acc.toInt())
                acc = ""
            }
        }

        if (char == '[') {
            stack.addLast(mutableListOf<Any>())
        }

        if (char == ']') {
            if (acc.isNotEmpty()) {
                stack.last().add(acc.toInt())
                acc = ""
            }

            val removedElement = stack.removeLast()
            stack.last().add(removedElement)
        }
    }

    return result.last() as List<Any>
}
