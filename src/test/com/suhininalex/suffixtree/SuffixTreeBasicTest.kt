package com.suhininalex.suffixtree

import org.junit.Before
import org.junit.Test


data class CharToken(val char: Char): Comparable<CharToken>{

    override fun toString(): String {
        return char.toString()
    }

    override fun compareTo(other: CharToken): Int =
        char.compareTo(other.char)
}

class SuffixTreeBasicTest {
    internal var tree = SuffixTree<CharToken>()
    internal val sequence = "cacao".map(::CharToken)

    @Test
    fun testTestAndSplit_noSplit() {
        println("Test test and split (no split)")
        println(tree)
        tree.testAndSplit(tree.root, sequence, 0, 3, CharToken('o'))
        println(tree)
    }

    @Test
    fun testTestAndSplit_split() {
        println("Test test and split (split)")
        println(tree)
        tree.testAndSplit(tree.root, sequence, 0, 3, CharToken('q'))
        println(tree)
    }

    @Test
    fun testCanonize_needed() {
        println("Test canonize (needed)")
        tree.testAndSplit(tree.root, sequence, 0, 2, CharToken('q'))
        println(tree)
        val canonizeRes = tree.canonize(tree.root, sequence, 0, 4)
        println("${canonizeRes.first} | ${canonizeRes.second}")
    }

    @Test
    fun testCanonize_unnecessary() {
        println("Test canonize (unnecessary)")
        tree.testAndSplit(tree.root, sequence, 0, 2, CharToken('q'))
        println(tree)
        val canonizeRes = tree.canonize(tree.root, sequence, 0, 1)
        println("${canonizeRes.first} | ${canonizeRes.second}")
    }

    @Test
    fun testCanonize_preroot() {
        println("Test canonize (pre root/null)")
        tree.testAndSplit(tree.root, sequence, 1, 2, CharToken('q'))
        println(tree)
        val canonizeRes = tree.canonize(null, sequence, 0, 4)
        println("${canonizeRes.first} | ${canonizeRes.second}")
    }

    @Before
    fun setUp() {
        tree.root.putEdge(sequence, 0)
        tree.root.putEdge(sequence, 1)
        println("-------------------------")
    }

}