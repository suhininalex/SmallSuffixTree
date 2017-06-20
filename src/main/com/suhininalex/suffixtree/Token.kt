package com.suhininalex.suffixtree

data class EndToken(val idSequence: Long) : Comparable<EndToken> {

    override fun toString(): String {
        return "#" + idSequence
    }

    override fun compareTo(other: EndToken): Int =
        this.idSequence.compareTo(other.idSequence)
}

val tokenComparator = java.util.Comparator<Any> { first, second ->
    if (first is EndToken) {
        return@Comparator if (second is EndToken) {
            first.compareTo(second)
        } else {
            1
        }
    } else {
        return@Comparator if (second is EndToken) {
            -1
        } else {
            (first as Comparable<Any>).compareTo(second)
        }
    }
}
