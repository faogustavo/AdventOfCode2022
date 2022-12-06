package dev.valvassori.day06

import dev.valvassori.readInput
import kotlin.streams.asSequence

fun main() {
    val message = readInput("day06").first()

    val startOfPacket = message.findStartOf(StartOfType.Packet)
    println(startOfPacket)

    val startOfMessage = message.findStartOf(StartOfType.Message)
    println(startOfMessage)
}

private enum class StartOfType(val size: Int) {
    Packet(4),
    Message(14),
}

private fun String.findStartOf(type: StartOfType): Int = chars()
    .asSequence()
    .windowed(type.size)
    .indexOfFirst { it.toSet().size == type.size } + type.size
