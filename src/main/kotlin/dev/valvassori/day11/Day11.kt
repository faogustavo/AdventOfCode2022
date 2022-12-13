package dev.valvassori.day11

import dev.valvassori.allMatches
import dev.valvassori.component6
import dev.valvassori.readInput

fun main() {
    fun simulate(rounds: Int, relief: Relief) {
        val monkeys = readInput("day11")
            .chunked(7)
            .map(Monkey::of)

        val operationCount: MutableMap<MonkeyIndex, Int> =
            monkeys.indices.associateWith { 0 }.toMutableMap()

        repeat(rounds) {
            monkeys.forEachIndexed { index, monkey ->
                var itCount = 0

                while(monkey.hasNext()) {
                    val (monkeyIndex, worryLevel) = monkey.next(relief)
                    monkeys[monkeyIndex].addItem(worryLevel)

                    itCount += 1
                }

                operationCount[index] = operationCount[index]!! + itCount
            }
        }

        val monkeyBusiness = operationCount.values.sorted()
            .takeLast(2)
            .map(Int::toLong)
            .reduce(WorryLevel::times)

        println("------------------------------------------")
        println("All operation counts: $operationCount")
        println("Monkey Business: $monkeyBusiness")
    }

    // Part 1
    simulate(20) { it / 3 }

    val lcm = readInput("day11")
        .chunked(7)
        .map(Monkey::of)
        .lcm()
    simulate(10000) { it % lcm }
}

private typealias MonkeyIndex = Int
private typealias WorryLevel = Long
private typealias Relief = (WorryLevel) -> WorryLevel

private data class Monkey(
    val operation: Operation,
    val test: Test,
    val outputMonkeyIfTrue: Int,
    val outputMonkeyIfFalse: Int,
    val initialItems: Collection<WorryLevel>,
) {
    val stack = ArrayDeque<WorryLevel>(initialItems)

    fun addItem(worryLevel: WorryLevel) {
        stack.addLast(worryLevel)
    }

    fun hasNext(): Boolean = stack.isNotEmpty()

    fun next(relief: Relief): Pair<MonkeyIndex, WorryLevel> {
        val operationResult = relief(operation.resolve(stack.removeFirst()))
        val outputMonkey = if (test.pass(operationResult)) outputMonkeyIfTrue else outputMonkeyIfFalse
        return outputMonkey to operationResult
    }

    companion object {
        private val monkeyNumberRegex = "Monkey (\\d+):".toRegex()
        private val outputConditionRegex = "If (true|false): throw to monkey (\\d+)".toRegex()
        fun of(lines: List<String>): Monkey {
            val (name, items, operation, test, firstCondition, secondCondition) = lines

            val allItems = items.split(":")
                .last()
                .split(",")
                .map { it.trim().toLong() }

            val conditions = mapOf<Boolean, MonkeyIndex>(
                outputConditionRegex.allMatches(firstCondition).parseMonkeyTestResult(),
                outputConditionRegex.allMatches(secondCondition).parseMonkeyTestResult(),
            )

            return Monkey(
                operation = Operation.of(operation),
                test = Test.of(test),
                outputMonkeyIfTrue = conditions[true]
                    ?: error("No true condition result for monkey '${lines.joinToString("\\n")}'"),
                outputMonkeyIfFalse = conditions[false]
                    ?: error("No false condition result for monkey '${lines.joinToString("\\n")}'"),
                initialItems = allItems,
            )
        }
    }

    sealed class Test {
        abstract fun pass(value: WorryLevel): Boolean
        data class Divisible(val divisor: WorryLevel) : Test() {
            override fun pass(value: WorryLevel): Boolean = value.mod(divisor) == 0L
        }

        companion object {
            private val testRegex = "Test: (.*) by (.*)".toRegex()
            fun of(line: String): Test {
                val (operation, value) = testRegex.allMatches(line)

                return when (operation) {
                    "divisible" -> Divisible(value.toLong())
                    else -> error("Invalid test line: '$line'")
                }
            }
        }
    }

    sealed class Operation {
        abstract fun resolve(input: WorryLevel): WorryLevel

        data class Sum(val addend: WorryLevel) : Operation() {
            override fun resolve(input: WorryLevel): WorryLevel = input + addend
        }

        data class Multiplication(val multiplier: WorryLevel) : Operation() {
            override fun resolve(input: WorryLevel): WorryLevel = input * multiplier
        }

        object Squared : Operation() {
            override fun resolve(input: WorryLevel): WorryLevel = input * input
        }

        companion object {
            private val lineRegex = "Operation: new = (.*) (\\+|\\*) (.*)".toRegex()
            fun of(line: String): Operation {
                val (arg1, operation, arg2) = lineRegex.allMatches(line)

                if (arg1 == "old" && arg2 == "old") {
                    return when (operation) {
                        "*" -> Squared
                        "+" -> Multiplication(2)
                        else -> error("Invalid operation '$operation'")
                    }
                }

                val numberArgument = arg1.toLongOrNull()
                    ?: arg2.toLongOrNull()
                    ?: error("None of the arguments are a number for line '$line'")

                return when (operation) {
                    "*" -> Multiplication(numberArgument)
                    "+" -> Sum(numberArgument)
                    else -> error("Invalid operation '$operation'")
                }
            }
        }
    }
}

// https://www.mathsisfun.com/least-common-multiple.html
private fun List<Monkey>.lcm(): WorryLevel =
    map { it.test }
        .map { if (it is Monkey.Test.Divisible) it.divisor else 1 }
        .distinct()
        .fold(1L) { acc, it -> acc * it }

private fun List<String>.parseMonkeyTestResult(): Pair<Boolean, MonkeyIndex> =
    first().toBooleanStrict() to last().toInt()
