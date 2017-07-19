package com.suhininalex.suffixtree

import java.util.*
import java.util.concurrent.atomic.AtomicLong

class SuffixTree<T : Comparable<T>> {

    val root = Node(null)

    internal val sequences: MutableMap<Long, List<Any>> = HashMap()

    private val sequenceFreeId = AtomicLong(1)
    private val nextFreeSequenceId: Long
        get() = sequenceFreeId.incrementAndGet()

    internal fun testAndSplit(s: Node, sequence: List<Any>, k: Int, p: Int, t: Any): Pair<Boolean, Node> {
        if (k <= p) {
            var ga = s.getEdge(sequence[k])!!
            if (t == ga.sequence[ga.k + p - k + 1])
                return Pair(true, s)
            else {
                val r = Node(ga)
                val newEdge = r.putEdge(
                        ga.terminal,
                        ga.sequence,
                        (ga.k + p - k + 1).toShort(),
                        ga.p
                )
                ga.terminal?.parentEdge = newEdge
                ga = ga.setTerminal(r)
                ga.p = (ga.k + p - k).toShort()
                return Pair(false, r)
            }
        } else {
            if (s.getEdge(t) == null)
                return Pair(false, s)
            else
                return Pair(true, s)
        }
    }

    internal fun canonize(s: Node?, sequence: List<Any>, k: Int, p: Int): Pair<Node?, Int> {
        var s = s
        var k = k
        if (s == null) {
            s = root
            k = k + 1
        }
        if (p < k)
            return Pair<Node, Int>(s, k)
        else {
            var ga: Edge = s.getEdge(sequence[k])!!

            while (ga.p - ga.k <= p - k) {
                k = k + ga.p - ga.k + 1
                s = ga.terminal
                if (k <= p) ga = s!!.getEdge(sequence[k])!!
            }
            return Pair(s, k)
        }
    }

    internal fun update(s: Node, sequence: List<Any>, k: Int, i: Int): Pair<Node, Int> {
        var s = s
        var k = k
        var oldr = root
        var splitRes = testAndSplit(s, sequence, k, i - 1, sequence[i])
        var endPoint = splitRes.first
        var r = splitRes.second
        while (!endPoint) {
            r.putEdge(sequence, i.toShort())
            if (oldr != root) oldr.suffixLink = r
            oldr = r
            val canonizeRes = canonize(s.suffixLink, sequence, k, i - 1)
            s = canonizeRes.first!!
            k = canonizeRes.second
            splitRes = testAndSplit(s, sequence, k, i - 1, sequence[i])
            endPoint = splitRes.first
            r = splitRes.second
        }
        if (oldr != root) oldr.suffixLink = r
        return Pair(s, k)
    }

    internal fun updateSequence(sequence: List<Any>) {
        var s = root
        var k = 0
        var i = -1
        while (i + 1 < sequence.size) {
            i += 1
            val updateRes = update(s, sequence, k, i)
            s = updateRes.first
            k = updateRes.second
            val canonizeRes = canonize(s, sequence, k, i)
            s = canonizeRes.first!!
            k = canonizeRes.second
        }
    }

    internal fun relabelAllParents(edge: Edge?) {
        var edge = edge
        while (edge != null && edge.parent != root) {
            val parentEdge = edge.parent.parentEdge!!
            if (parentEdge.sequence === edge.sequence) return
            parentEdge.sequence = edge.sequence
            parentEdge.k = (edge.k.toInt() - (parentEdge.p - parentEdge.k) - 1).toShort()
            parentEdge.p = (edge.k - 1).toShort()
            edge = parentEdge
        }
    }

    fun removeSequence(id: Long) {
        val sequence = sequences[id] ?: throw IllegalArgumentException("There are no such sequence!")
        sequences.remove(id)
        var currentPoint: Pair<Node?, Int> = Pair(root, 0)
        do {
            val (node, k) = canonize(currentPoint.first, sequence, currentPoint.second, sequence.size - 2)
            currentPoint = removeEdge(node!!, sequence, k)
            relabelAllParents(currentPoint.first!!.firstEdge)
            currentPoint = Pair(currentPoint.first!!.suffixLink, currentPoint.second)
        } while (currentPoint.second < sequence.size - 1 || currentPoint.first != null)
    }

    internal fun removeEdge(s: Node, sequence: List<Any>, k: Int): Pair<Node?, Int> {
        val edge = s.getEdge(sequence[k])!!
        s.removeEdge(edge)
        if (s != root && s.containsOneEdge()) {
            val anotherChild = s.firstEdge!!
            var parentEdge = s.parentEdge!!
            parentEdge = parentEdge.setTerminal(anotherChild.terminal)
            if (anotherChild.terminal != null) anotherChild.terminal!!.parentEdge = parentEdge
            val parentEdgeLength = parentEdge.p - parentEdge.k + 1
            parentEdge.k = (anotherChild.k - parentEdgeLength).toShort()
            parentEdge.p = anotherChild.p
            parentEdge.sequence = anotherChild.sequence
            return Pair(parentEdge.parent, k - parentEdgeLength)
        }
        return Pair(s, k)
    }

    fun checkSequence(sequence: List<T>): Boolean {
        if ( sequence.isEmpty()) return true
        var k = 0
        var currentNode: Node? = root
        do {
            val currentEdge = currentNode!!.getEdge(sequence[k]) ?: return false
            for (i in currentEdge.k..currentEdge.p) {
                if (sequence[k] != currentEdge.sequence[i]) return false
                k++
                if (k >= sequence.size) return true
            }
            currentNode = currentEdge.terminal
        } while (currentNode != null)
        return false
    }

    fun addSequence(sequence: List<T>): Long {
        if (sequence.size + 1 > Short.MAX_VALUE) throw IllegalArgumentException("Sequence length should be less then ${Short.MAX_VALUE}")
        val tokens = ArrayList<Any>()
        val idSequence = nextFreeSequenceId
        tokens.addAll(sequence)
        tokens.add(EndToken(idSequence))
        tokens.trimToSize()
        sequences.put(idSequence, tokens)
        updateSequence(tokens)
        return idSequence
    }

    fun getSequence(id: Long): List<T> {
        val sequence = sequences[id] ?: throw IllegalStateException("No such sequence!")
        return sequence.subList(0, sequence.size - 1) as List<T> //drop end token
    }

    fun getFullSequence(id: Long): List<Any> {
        return sequences[id] ?: throw IllegalStateException("No such sequence!")
    }

    override fun toString(): String {
        return root.subTreeToString()
    }

    fun getAllLastSequenceNodes(id: Long): List<Node> {
        val sequence = sequences[id] ?: throw IllegalArgumentException("There are no such sequence!")
        val nodes = LinkedList<Node>()

        for (i in 0..sequence.size - 1 - 1) { //drop end token
            nodes.add(canonize(root, sequence, i, sequence.size - 2).first!!)
        }
        return nodes
    }
}
