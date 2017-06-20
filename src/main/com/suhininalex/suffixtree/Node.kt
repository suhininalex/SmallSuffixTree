package com.suhininalex.suffixtree


class Node internal constructor(var parentEdge: Edge?) : ScapeGoatTree<Any, Edge>() {

    var suffixLink: Node? = null

    internal fun putEdge(terminal: Node?, sequence: List<Any>, k: Short, p: Short): Edge {
        val edge = if (terminal == null) {
             TerminalEdge(this, sequence, k, p)
        } else {
             InternalEdge(this, terminal, sequence, k, p)
        }
        this.insert(edge)
        return edge
    }

    internal fun putEdge(sequence: List<Any>, k: Short): Edge {
        val edge = TerminalEdge(this, sequence, k, (sequence.size - 1).toShort())
        this.insert(edge)
        return edge
    }

    fun getEdge(token: Any): Edge? {
        return this.get(token)?.value
    }

    internal fun removeEdge(edge: Edge) {
        remove(edge.key)
    }

    private fun printToStringBuilder(out: StringBuilder, prefix: String) {
        out.append(prefix).append(this).append("\n")
        for (edge in edges) {
            out.append(prefix).append(edge).append("\t $suffixLink").append("\n")
            if (edge.terminal != null) edge.terminal!!.printToStringBuilder(out, prefix + "    ")
        }
    }

    fun subTreeToString(): String {
        val out = StringBuilder()
        this.printToStringBuilder(out, "")
        return out.toString()
    }

    val edges: Collection<Edge>
        get() = entries().map { it.value }

}