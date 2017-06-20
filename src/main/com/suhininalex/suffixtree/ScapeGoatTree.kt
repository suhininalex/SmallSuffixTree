package com.suhininalex.suffixtree

import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

private val alpha: Double = 0.58

val comparator: Comparator<Any> = tokenComparator

open class ScapeGoatTree<K, V>{
    internal var root: ScapeGoatNode<K, V>? = null
    internal var size: Int = 0
    internal var maxSize: Int = 0
}

abstract class ScapeGoatNode<K, V>{
    internal var left: ScapeGoatNode<K, V>? = null
    internal var right: ScapeGoatNode<K, V>? = null
    internal abstract val key: K
    internal abstract val value: V
}

internal fun <K, V> ScapeGoatNode<K, V>.getOrSetLeft(element: ScapeGoatNode<K, V>): ScapeGoatNode<K, V> {
    if (left == null) {
        left = element
    }
    return left!!
}

internal fun h_alpha(alpha: Double, size: Int): Int {
    return -log(value = size.toDouble(), base = alpha).toInt()
}

internal fun <K, V> ScapeGoatNode<K, V>.getOrSetRight(element: ScapeGoatNode<K, V>): ScapeGoatNode<K, V> {
    if (right == null) {
        right = element
    }
    return right!!
}

internal fun <K, V> ScapeGoatTree<K, V>.put(element: ScapeGoatNode<K, V>): Stack<ScapeGoatNode<K, V>> {
    size += 1
    maxSize = size
    if (root == null) {
        root = element
        return Stack()
    } else {
        var current = root!!
        val parents = Stack<ScapeGoatNode<K, V>>()
        while (current !== element) {
            parents.push(current)
            val cmp = comparator.compare(element.key, current.key)
            if (cmp < 0) current = current.getOrSetLeft(element)
            else if (cmp > 0) current = current.getOrSetRight(element)
            else throw IllegalArgumentException("Duplicate element $element with $current")
        }
       return parents
    }
}

internal fun <K, V> ScapeGoatTree<K, V>.delete(key: K) {
    var current = root
    var parent: ScapeGoatNode<K, V>? = null
    while (current != null) {
        val cmp = comparator.compare(key, current.key)
        if (cmp < 0) { parent = current; current = current.left }
        else if (cmp > 0) { parent = current; current = current.right }
        else {
            if (current.hasBothChildren()){
                current.checkIsChild(current.right)
                val (minNode, minParent) = findMinimum(current.right!!, current)
                minParent?.checkIsChild(minNode)
                deleteIfHasEmptyChild(minNode, minParent)
                minNode.right = current.right
                minNode.left = current.left
                replaceChild(parent, current, minNode)
            } else {
                deleteIfHasEmptyChild(current, parent)
            }
            return
        }
    }
}

internal fun <K, V> ScapeGoatTree<K, V>.remove(key: K): Unit {
    delete(key)
    if (size < alpha*maxSize) {
        if (root != null) {
            root = Rebuild_Tree(size, root!!).first
        }
    }
}

internal fun <K, V> ScapeGoatTree<K, V>.get(key: K): ScapeGoatNode<K, V>? {
    val r = root.search(key)
    return r
}

internal tailrec fun <K, V> ScapeGoatNode<K, V>?.search(key: K): ScapeGoatNode<K, V>? {
    if (this == null) return null
    val cmp = comparator.compare(key, this.key)
    if (cmp > 0) return right.search(key)
    else if (cmp < 0) return left.search(key)
    else return this
}

internal fun <K, V> findScapeGoat(parents: Stack<ScapeGoatNode<K, V>>, node: ScapeGoatNode<K, V>): Pair<ScapeGoatNode<K, V>, ScapeGoatNode<K, V>?> {
    var n = node
    var size = 1
    var height = 0
    while (parents.isNotEmpty()){
        val parent = parents.pop()
        height += 1
        val totalSize = 1 + size + siblingOf(parent, n).size()
        val h_alpha = h_alpha(alpha, totalSize)
        if (height > h_alpha) return parent to parents.lastOrNull()
        n = parent
        size = totalSize
    }
    throw IllegalStateException("There is no scapegoat among parents.")
}

tailrec internal fun <K, V> findMinimum(node: ScapeGoatNode<K, V>, nodeParent: ScapeGoatNode<K, V>?): Pair<ScapeGoatNode<K, V>, ScapeGoatNode<K, V>?> {
    nodeParent?.checkIsChild(node)
    if (node.left != null)
        return findMinimum(node.left!!, node)
    else return Pair(node, nodeParent)
}

internal fun <K, V> ScapeGoatNode<K, V>.hasBothChildren() =
    left != null && right != null

internal fun <K, V> ScapeGoatTree<K, V>.deleteIfHasEmptyChild(node: ScapeGoatNode<K, V>, parent: ScapeGoatNode<K, V>?){
    parent?.checkIsChild(node)
    if (node.hasBothChildren()) throw IllegalArgumentException("Has both children.")
    val newNode = node.right ?: node.left
    replaceChild(parent, node, newNode)
    size -= 1

}

internal fun <K, V> ScapeGoatTree<K, V>.replaceChild(parent: ScapeGoatNode<K, V>?, oldChild: ScapeGoatNode<K, V>, newChild: ScapeGoatNode<K, V>?){
    parent?.checkIsChild(oldChild)
    if (parent == null){
        root = newChild
    } else if (parent.left === oldChild){
        parent.left = newChild
    } else if (parent.right === oldChild){
        parent.right = newChild
    } else {
        throw IllegalArgumentException("OldChild: ${oldChild.key} New: ${newChild?.key} Parent: ${parent.key} ${parent.left?.key} ${parent.right?.key}")
    }
}

internal fun <K, V> ScapeGoatNode<K, V>.checkIsChild(child: ScapeGoatNode<K, V>?){
    if (left !== child && right !== child)
        throw IllegalArgumentException("Parent: $key ${left?.key} ${right?.key} Child: ${child?.key}")
}

internal fun <K, V> ScapeGoatTree<K, V>.entries(): Collection<ScapeGoatNode<K, V>>{
    return root.children(ArrayList())
}

internal fun <K, V> ScapeGoatNode<K, V>?.children(accumulator: MutableList<ScapeGoatNode<K, V>>): Collection<ScapeGoatNode<K, V>>{
    if (this != null){
        accumulator.add(this)
        left.children(accumulator)
        right.children(accumulator)
    }
    return accumulator
}

internal fun <K, V> siblingOf(parent: ScapeGoatNode<K, V>, node: ScapeGoatNode<K, V>): ScapeGoatNode<K, V>? =
    if (parent.left === node)  parent.right
    else if (parent.right === node)  parent.left
    else throw IllegalArgumentException("Node $node must be a child of parent.")

internal fun <K, V> ScapeGoatNode<K, V>?.size(): Int =
    if (this != null) left.size() + right.size() + 1
    else 0

internal fun <K, V> flatten_tree(root: ScapeGoatNode<K, V>?, tail: ScapeGoatNode<K, V>? = null): ScapeGoatNode<K, V>? {
    if (root == null) return tail
    root.right = flatten_tree(root.right, tail)
    val result = flatten_tree(root.left, root)
    root.left = null
    return result
}

internal fun <K, V> Build_Height_Balanced_Tree(size: Int, head: ScapeGoatNode<K, V>?): Pair<ScapeGoatNode<K, V>?, ScapeGoatNode<K, V>?> {
    if (size == 1) {
        return Pair(head, head)
    }
    if (size == 2) {
        val root = head!!.right!!
        root.left = head
        head.right = null
        return Pair(root, root)
    }
    val (leftRoot, leftLast) = Build_Height_Balanced_Tree(size/2, head)
    val root = leftLast?.right ?: head

    root?.left = leftRoot

    val (rightRoot, rightLast) = Build_Height_Balanced_Tree(size - size/2 - 1, root?.right)
    root?.right = rightRoot

    leftLast?.right = null
    return Pair(root, rightLast)
}

internal fun <K, V> Rebuild_Tree(size: Int, scapegoat: ScapeGoatNode<K, V>): Pair<ScapeGoatNode<K, V>?, ScapeGoatNode<K, V>?> {
    val head = flatten_tree(scapegoat, null)!!
    val result = Build_Height_Balanced_Tree(size, head)
    return result
}

internal fun <K, V> ScapeGoatTree<K, V>.insert(node: ScapeGoatNode<K, V>){
    val parents = put(node)
    val height = parents.size
    val h_alpha = h_alpha(alpha, size)
    if (height > h_alpha){
        val (scapegoat, scapeGoatParent) = findScapeGoat(parents, node)
        val isLeft = scapeGoatParent?.left === scapegoat
        val (root, last) = Rebuild_Tree(scapegoat.size(), scapegoat)
        if (scapeGoatParent == null) this.root = root
        else if (isLeft) scapeGoatParent.left = root
        else scapeGoatParent.right = root
    }
}

private fun log(value: Double, base: Double): Double =
        Math.log(value)/Math.log(base)