package dev.valvassori

import java.io.File

fun inputFileForName(name: String) = File("src/main/kotlin/dev/valvassori/", "$name/input.txt")

fun readInput(name: String): List<String> = inputFileForName(name).readLines()

fun String.splintIntoDigits() = toCharArray().toList().charsToIntList()

fun List<String>.toIntList() = map { it.toInt() }
fun List<Char>.charsToIntList() = map { it.digitToInt() }
fun List<List<String>>.toListOfIntList(): List<List<Int>> = map { it.toIntList() }

operator fun <T> List<T>.component6(): T = get(5)

fun Regex.allMatches(value: String): List<String> = find(value)?.groupValues?.drop(1).orEmpty()
