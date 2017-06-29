package com.suhininalex.suffixtree

import org.junit.Test
import java.util.*
import kotlin.system.measureTimeMillis

data class LongToken(val value: Long): Comparable<LongToken>{

    override fun toString(): String {
        return value.toString()
    }

    override fun compareTo(other: LongToken): Int =
        value.compareTo(other.value)
}

class SuffixTreeComplexTest {
    internal var sequencesAmount = 1000
    internal var sequencesLength = 200
    internal var sequencesRemovals = 800

    fun estimateMemory(amount: Int, length: Int){
        val tree = createTree(amount, length)
        System.gc()
        val memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        println(memory / amount / length)
    }

    fun createTree(amount: Int, length: Int): SuffixTree<LongToken> {
        val sequences = generateRandomLists(amount, length)
        val tree = SuffixTree<LongToken>()
        sequences.forEach {
            tree.addSequence(it)
        }
        return tree
    }


    @Test
    fun testComplexRemoveSequence() {
        println("Complex tree testing")
        val tree = SuffixTree<LongToken>()
        val sequences = generateRandomLists(sequencesAmount, sequencesLength)
        val sequencesId = ArrayList<Long>()

        print("Adding...")
        val additionTime = measureTimeMillis {
            for (sequence in sequences) {
                sequencesId += tree.addSequence(sequence)
            }
        }
        println("OK in $additionTime")


        print("Testing added sequences...")
        checkSequences(tree, sequences)
        println("OK")

        val treeSequences = sequencesId.zip(sequences)
        val removedIds = treeSequences.shuffle().take(sequencesRemovals).map { (id, sequence) -> id }.toSet()

        print("Removing...")
        val removingTime = measureTimeMillis {
            for (id in removedIds) {
                tree.removeSequence(id)
            }
        }
        println("OK in $removingTime")


        val remained = treeSequences.filter { (id, sequence) -> id !in removedIds }.map {(id, sequence) -> sequence}
        print("Check other existance...")
        checkSequences(tree, remained)
        println("OK")


        val removed = treeSequences.filter { (id, sequence) -> id in removedIds }.map {(id, sequence) -> sequence}
        print("Check relabelling and edges removal...")
        for (sequence in removed) {
            assert(checkNoSequence(tree.root, sequence))
        }
        println("OK")

        print("Check no removed sequence...")
        assert(removed.all { ! tree.checkSequence(it) })
        println("OK")

    }

    private fun checkSequences(tree: SuffixTree<LongToken>, sequences: List<List<LongToken>>) {
        for (sequence in sequences) {
            assert(checkAllSuffixes(tree, sequence)){ "Some suffixes are missing!" }
        }
    }

    private fun checkNoSequence(node: Node?, sequence: List<*>): Boolean {
        if (node == null) return true
        for (edge in node.edges) {
            if (edge.sequence === sequence) {
                println(node.toString() + "|" + edge)
                return false
            }
            if (!checkNoSequence(edge.terminal, sequence)) return false
        }
        return true
    }

    private fun <T: Comparable<T>> checkAllSuffixes(tree: SuffixTree<T>, sequence: List<T>): Boolean {
        return sequence.indices.all { tree.checkSequence(sequence.subList(it, sequence.size)) }
    }

    private fun generateRandomLists(amount: Int, length: Int): MutableList<List<LongToken>> {
        val lists = ArrayList<List<LongToken>>()
        for (i in 0..amount - 1) {
            lists.add(generateRandomList(length))
        }
        return lists
    }

    private fun generateRandomList(length: Int): List<LongToken> {
        val result = ArrayList<LongToken>()
        for (i in 0..length - 1) {
            result.add(randomToken())
        }
        return result
    }

    val random = Random()

    private fun randomToken(setSize: Int = 1000): LongToken {
        return LongToken(random.nextInt(setSize).toLong())
    }

    private fun getRandom(maxInt: Int): Int {
        return (Math.random() * maxInt).toInt()
    }
}
