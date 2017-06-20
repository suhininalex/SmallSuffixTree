package com.suhininalex.suffixtree

import org.junit.Test


class SuffixTreeRemovingTest {

    internal var tree = SuffixTree<CharToken>()
    internal var sequence1 = "cacao".map(::CharToken)
    internal var sequence2 = "cacaoa".map(::CharToken)

    @Test
    fun testSimpleRemoveSequence() {
        println("Test simple remove sequence")
        tree.addSequence(sequence1)
        val id2 = tree.addSequence(sequence2)
        println(tree)
        tree.removeSequence(id2)
        println(tree)
    }

    //    @Test
    /*
        Test with visualvm
        (OK)
    */
    //    public void testMemoryLeakWhenRemoveSequence() {
    //        System.out.println("Test complex remove sequence");
    //        tree.addSequence(sequence1);
    //        System.out.println(tree);
    //        for (int i=0;i<1000000000; i++){
    //            long id = tree.addSequence(sequence2);
    //            tree.removeSequence(id);
    //        }
    //        System.out.println(tree);
    //    }

}