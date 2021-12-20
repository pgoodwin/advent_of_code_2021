import java.io.File

fun main() {
    val polymerFileLines = File("polymer_rules.txt").readLines()
    val template = polymerFileLines.first().toList()

    val polymerRuleRegEx = "(\\w\\w) -> (\\w)".toRegex()
    val polymerRules = polymerFileLines.drop(2).map {
        val (_, elementPair, insertionValue) = polymerRuleRegEx.find(it)!!.groupValues
        Pair(elementPair.toList(), insertionValue.toList().single())
    }.toMap().also(::println)

    val chainAfter10 = (1..10).fold(template) { chain, _ ->
        applyInsertionRules(chain, polymerRules)
    }
    val elementFrequencies = chainAfter10.groupingBy { it }.eachCount()
    val sortedFrequencies = elementFrequencies.toList().sortedBy { it.second }.also(::println)
    println(sortedFrequencies.last().second - sortedFrequencies.first().second)
}

private fun applyInsertionRules(chain: List<Char>, polymerRules: Map<List<Char>, Char>): List<Char> {
    return chain.zipWithNext().flatMap {
        listOf(it.first, polymerRules[it.toList()]!!)
    }.plus(chain.last()).also(::println)
}
