package dev.valvassori.day08

import dev.valvassori.readInput
import dev.valvassori.splintIntoDigits
import kotlin.math.max

fun main() {
    val grid: List<List<Int>> =
        readInput("day08").map { it.splintIntoDigits() }

    println(
        grid.sumOfIndexed { treeRow, row ->
            row.countWithIndexed { treeColumn, _ -> grid.isVisible(treeRow, treeColumn) }
        }
    )

    println(
        grid.mapIndexed { treeRow, row ->
            row.mapIndexed { treeColumn, _ -> grid.scenicScore(treeRow, treeColumn) }
        }.flatten().max()
    )
}

private fun List<List<Int>>.isVisible(treeRow: Int, treeColumn: Int): Boolean {
    // When it's in the edge, always count
    if (treeRow == 0 || treeColumn == 0 || treeRow == lastIndex || treeColumn == this[treeRow].lastIndex)
        return true

    val currentValue = this[treeRow][treeColumn]

    val rowCountToBottom = this.size - treeRow
    val columnCountToRight = this[treeRow].size - treeColumn
    val iterations = setOf(treeRow, treeColumn, rowCountToBottom, columnCountToRight).reduce(::max)

    var checkTop = true
    var visibleToTop = true

    var checkBottom = true
    var visibleToBottom = true

    var checkLeft = true
    var visibleToLeft = true

    var checkRight = true
    var visibleToRight = true

    for (offset in 1..iterations) {
        if (checkTop) {
            val topValueToCheck = treeRow - offset
            if (topValueToCheck == 0) checkTop = false

            val iterationValue = this[topValueToCheck][treeColumn]
            if (iterationValue >= currentValue) {
                visibleToTop = false
                checkTop = false
            }
        }

        if (checkBottom) {
            val bottomValueToCheck = (treeRow + offset).coerceAtMost(lastIndex)
            if (bottomValueToCheck == lastIndex) checkBottom = false

            val iterationValue = this[bottomValueToCheck][treeColumn]
            if (iterationValue >= currentValue) {
                visibleToBottom = false
                checkBottom = false
            }
        }

        val cachedRow = this[treeRow]
        if (checkLeft) {
            val leftValueToCheck = treeColumn - offset
            if (leftValueToCheck == 0) checkLeft = false

            val iterationValue = cachedRow[leftValueToCheck]
            if (iterationValue >= currentValue) {
                visibleToLeft = false
                checkLeft = false
            }
        }

        if (checkRight) {
            val rightValueToCheck = (treeColumn + offset).coerceAtMost(cachedRow.lastIndex)
            if (rightValueToCheck == cachedRow.lastIndex) checkRight = false

            val iterationValue = cachedRow[rightValueToCheck]
            if (iterationValue >= currentValue) {
                visibleToRight = false
                checkRight = false
            }
        }

        if (setOf(visibleToTop, visibleToBottom, visibleToLeft, visibleToRight).none { it }) {
            break
        }
    }

    return setOf(visibleToTop, visibleToBottom, visibleToLeft, visibleToRight).any { it }
}

private fun List<List<Int>>.scenicScore(treeRow: Int, treeColumn: Int): Int {
    val currentValue = this[treeRow][treeColumn]
    val cachedRow = this[treeRow]

    val rowCountToBottom = this.size - treeRow
    val columnCountToRight = cachedRow.size - treeColumn
    val iterations = setOf(treeRow, treeColumn, rowCountToBottom, columnCountToRight).reduce(::max)

    var checkTop = treeRow > 0
    var countToTop = 0

    var checkBottom = treeRow < lastIndex
    var countToBottom = 0

    var checkLeft = treeColumn > 0
    var countToLeft = 0

    var checkRight = treeColumn < cachedRow.lastIndex
    var countToRight = 0

    for (offset in 1..iterations) {
        if (checkTop) {
            countToTop += 1

            val index = (treeRow - offset).coerceAtLeast(0)
            val value = this[index][treeColumn]

            if (index == 0 || index == lastIndex || value >= currentValue) checkTop = false
        }

        if (checkBottom) {
            countToBottom += 1

            val index = (treeRow + offset).coerceAtMost(lastIndex)
            val value = this[index][treeColumn]

            if (index == 0 || index == lastIndex || value >= currentValue) checkBottom = false
        }

        if (checkLeft) {
            countToLeft += 1

            val index = (treeColumn - offset).coerceAtLeast(0)
            val value = cachedRow[index]

            if (index == 0 || index == lastIndex || value >= currentValue) checkLeft = false
        }

        if (checkRight) {
            countToRight += 1

            val index = (treeColumn + offset).coerceAtMost(cachedRow.lastIndex)
            val value = cachedRow[index]

            if (index == 0 || index == cachedRow.lastIndex || value >= currentValue) checkRight = false
        }
    }

    return listOf(
        countToRight,
        countToLeft,
        countToTop,
        countToBottom,
    ).reduce(Int::times)
}

private fun <T> List<T>.countWithIndexed(block: (index: Int, value: T) -> Boolean): Int {
    var sum = 0
    forEachIndexed { index, value ->
        if (block(index, value)) sum += 1
    }
    return sum
}

private fun <T> List<T>.sumOfIndexed(block: (index: Int, value: T) -> Int): Int {
    var sum = 0
    forEachIndexed { index, value ->
        sum += block(index, value)
    }
    return sum
}
