package dev.valvassori.day07

import dev.valvassori.readInput
import java.math.BigInteger

private val THRESHOLD_100K = BigInteger.valueOf(100_000)
private val DEVICE_STORAGE = BigInteger("70000000")
private val REQUIRED_SIZE_FOR_UPDATE = BigInteger("30000000")

fun main() {
    val root = readInput()

    // Step 1
    fun allDirsSmallerThen100K(root: FileTreeNode.Dir): List<FileTreeNode> =
        root.dirs.filter { it.size <= THRESHOLD_100K } + root.dirs.map { allDirsSmallerThen100K(it) }.flatten()

    val allNodesAtMost100K = allDirsSmallerThen100K(root)
    println(allNodesAtMost100K.sumOf { it.size })


    // Step 2
    val currentFreeStorage = (DEVICE_STORAGE - root.size)
    val requiredFreeStorage = REQUIRED_SIZE_FOR_UPDATE - currentFreeStorage

    fun allDirsGreaterThenUpdateThreshold(root: FileTreeNode.Dir): List<FileTreeNode> =
        root.dirs.filter { it.size >= requiredFreeStorage } + root.dirs.map { allDirsGreaterThenUpdateThreshold(it) }.flatten()

    val allNodesThatCouldBeDeleted = allDirsGreaterThenUpdateThreshold(root)
    println(allNodesThatCouldBeDeleted.minBy { it.size }.size)
}

sealed class FileTreeNode {
    abstract val name: String
    abstract val parent: Dir?
    abstract val size: BigInteger
    abstract fun walk(path: String): Dir

    data class File(
        override val parent: Dir?,
        override val name: String,
        override val size: BigInteger,
    ) : FileTreeNode() {
        override fun walk(path: String): Dir =
            error("File cannot walk")
    }

    data class Dir(
        override val parent: Dir?,
        override val name: String,
    ) : FileTreeNode() {
        private val _files = mutableMapOf<String, FileTreeNode>()

        override val size: BigInteger get() = files.sumOf { it.size }
        val files: List<FileTreeNode> get() = _files.values.toList()
        val dirs: List<Dir> get() = files.filterIsInstance<Dir>()
        fun add(node: FileTreeNode) {
            _files[node.name] = node
        }

        override fun walk(path: String): Dir =
            (if (path == "..") parent else _files[path] as? Dir) ?: error("Directory does not exist")
    }

    companion object {
        fun of(line: String, parent: Dir?) : FileTreeNode {
            val (firstPart, secondPart) = line.split(" ")
            return when {
                "dir".equals(firstPart, true) -> Dir(parent, secondPart)
                firstPart.toLongOrNull() != null -> File(parent, secondPart, firstPart.let(::BigInteger))
                else -> error("Invalid line '$line'")
            }
        }
    }
}

private fun readInput(): FileTreeNode.Dir {
    val fileData = readInput("day07").drop(1)

    val rootDir: FileTreeNode.Dir = FileTreeNode.Dir(null, "/")
    var currentNode = rootDir

    fileData.forEach { line ->
        when {
            line.startsWith("\$ cd") -> {
                // Change Dir
                currentNode = currentNode.walk(line.split(" ").last())
            }
            line.startsWith("\$ ls") -> {
                // Do Nothing
            }
            else -> {
                currentNode.add(FileTreeNode.of(line, currentNode))
            }
        }
    }


    return rootDir
}
