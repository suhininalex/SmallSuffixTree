## Small Suffix Tree

Kotlin Suffix Tree extended implementation. Also can be used from Java.

Main features:
  * Opportunity to work with any type of tokens
  * Relatively small memory consumption (about 100 bytes per token)
  * Opportunity to work incrementally with number of strings
  * Opportunity to delete certain string from the tree
  * Opportunity to add strings with size up to 32766 

## Code sample
```kotlin
data class Token(val value: Int): Comparable<Token>{
    override fun compareTo(other: Token) = value.compareTo(other.value)
}
```

```kotlin
val sequence: List<Token> = <...>
val tree = SuffixTree<Token>()
val id = tree.add(sequence)
tree.removeSequence(id)
```
