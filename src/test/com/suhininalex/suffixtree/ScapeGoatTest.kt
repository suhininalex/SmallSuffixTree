package com.suhininalex.suffixtree

import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList

class ScapeGoatTest {

    val elements = 10000
    val toRemove = 9000

    @Test
    fun complexTest(){
        val scapeGoatTree = MyTree()

        val elements = createElementSequence(this.elements).toSet()
        elements.map(::MyNode).forEach { scapeGoatTree.insert(it) }
        val allInTree = elements.all { scapeGoatTree.get(it)!=null }
        assert(allInTree)

        val elementsToRemove = elements.toList().shuffle().take(toRemove)
        elementsToRemove.forEach {
            scapeGoatTree.remove(it)
        }
        val allRemain = (elements - elementsToRemove).all { scapeGoatTree.get(it)!=null }
        val allRemoved = elementsToRemove.all { scapeGoatTree.get(it) == null }
        assert(allRemain)
        assert(allRemoved)
    }
}

fun <T> List<T>.shuffle(): List<T>{
    val list = ArrayList(this)
    Collections.shuffle(list)
    return list
}

class MyTree: ScapeGoatTree<Element, MyNode>()

data class MyNode(val element: Element): ScapeGoatNode<Element, MyNode>() {
    override val key: Element = element
    override val value: MyNode = this
}

fun createElementSequence(length: Int): List<Element>{
    return (1..length).map { Element(random.nextInt()) }
}

val random = Random()

data class Element(val value: Int): Comparable<Element>{
    override fun toString(): String {
        return value.toString()
    }

    override fun compareTo(other: Element): Int =
        value.compareTo(other.value)
}
