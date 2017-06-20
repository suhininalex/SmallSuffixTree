package com.suhininalex.suffixtree

import org.junit.Test
import java.util.*

data class LongToken(val value: Long): Comparable<LongToken>{

    override fun toString(): String {
        return value.toString()
    }

    override fun compareTo(other: LongToken): Int =
        value.compareTo(other.value)
}

class SuffixTreeComplexTest {
    internal var sequencesAmount = 1000
    internal var sequencesLength = 1000
    internal var sequencesRemovals = 500


    @Test
    fun t(){
        estimateMemory(100000, 100)
    }

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
        for (sequence in sequences) {
            val id = tree.addSequence(sequence)
            sequencesId.add(id)
        }


        print("Testing added sequences...")
        checkSequences(tree, sequences)
        println("OK")

        val removedId = ArrayList<Long>()
        val removedSequences = ArrayList<List<*>>()
        for (i in 0..sequencesRemovals - 1) {
            val index = getRandom(sequencesId.size)
            removedId.add(sequencesId[index])
            sequencesId.removeAt(index)
            sequences.removeAt(index)
        }

        for (id in removedId) {
            removedSequences.add(tree.sequences[id]!!)
        }


        print("Removing...")
        for (id in removedId) {
            tree.removeSequence(id)
        }
        println("OK")


        print("Check other existance...")
        checkSequences(tree, sequences)
        println("OK")


        print("Check relabelling and edges removal...")
        for (sequence in removedSequences) {
            assert(checkNoSequence(tree.root, sequence))
        }
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
        return sequence.indices.any { tree.checkSequence(sequence.subList(it, sequence.size)) }
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

    private fun randomToken(setSize: Int = 5): LongToken {
        return LongToken(random.nextInt(setSize).toLong())
    }

    private fun getRandom(maxInt: Int): Int {
        return (Math.random() * maxInt).toInt()
    }
}
