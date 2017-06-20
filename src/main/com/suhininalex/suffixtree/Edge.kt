package com.suhininalex.suffixtree


abstract class Edge : ScapeGoatNode<Any, Edge>() {
    /* header 12 bytes */
    abstract var parent: Node // 4 bytes
        internal set

    abstract var terminal: Node? //4 bytes or zero
        internal set

    abstract var sequence: List<Any> //4 bytes
        internal set

    abstract var k: Short //2 bytes
        internal set

    abstract var p: Short //2 bytes
        internal set

    internal val firstToken: Any
        get() = sequence[k.toInt()]

    override fun toString(): String {
        return sequence.subList(k.toInt(), p + 1).toString()
    }

    val begin: Int
        get() = k.toInt()

    val end: Int
        get() = p.toInt()

    /* total 24-28 bytes*/

    /* 8 bytes ScapeGoatNode */

    override val value: Edge
        get() = this

    override val key: Any
        get() = firstToken
}

fun Edge.setTerminal(terminal: Node?): Edge {
    if (terminal != null && this is TerminalEdge){
        val edge = InternalEdge(parent, terminal, sequence, k, p)
        parent.removeEdge(this)
        parent.insert(edge)
        terminal.parentEdge = edge
        return edge
    } else if (terminal == null && this is InternalEdge){
        val edge = TerminalEdge(parent, sequence, k, p)
        parent.removeEdge(this)
        parent.insert(edge)
        return edge
    } else {
        this.terminal = terminal
        return this
    }
}

class TerminalEdge

    internal constructor (
            override var parent: Node,
            override var sequence: List<Any>,
            override var k: Short,
            override var p: Short
    ) : Edge() {

    override var terminal: Node?
        get() = null
        set(value) { throw UnsupportedOperationException()}

}

class InternalEdge

    internal constructor(
            override var parent: Node,
            override var terminal: Node?,
            override var sequence: List<Any>,
            override var k: Short,
            override var p: Short
    ) : Edge()