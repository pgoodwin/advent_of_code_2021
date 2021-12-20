import java.io.File

fun main() {
    val polymerFileLines = File("polymer_rules.txt").readLines()
    val template = polymerFileLines.first().toList()

    val polymerRuleRegEx = "(\\w\\w) -> (\\w)".toRegex()
    val polymerRulesMap = polymerFileLines.drop(2).map {
        val (_, elementPair, insertionValue) = polymerRuleRegEx.find(it)!!.groupValues
        Pair(elementPair.toList(), insertionValue.toList().single())
    }.toMap()
    val maxElementIndex = polymerRulesMap.keys.flatten().maxOf{it}.also(::println) + 1
    val polymerRules = Array(maxElementIndex.toInt()) { CharArray(maxElementIndex.toInt()) { ' ' } }
    polymerRulesMap.forEach {
        polymerRules[it.key.first().toInt()][it.key.last().toInt()] = it.value
    }
    polymerRules.forEach { println(it) }

    val chainAfter10 = (1..10).fold(template) { chain, _ ->
        applyInsertionRules(chain, polymerRules)
    }
    val elementFrequencies = chainAfter10.groupingBy { it }.eachCount()
    val sortedFrequencies = elementFrequencies.toList().sortedBy { it.second }.also(::println)
    println(sortedFrequencies.last().second - sortedFrequencies.first().second)
}

private fun applyInsertionRules(chain: List<Char>, polymerRules: Array<CharArray>): List<Char> {
    return chain.zipWithNext().flatMap {
        listOf(it.first, polymerRules[it.first.toInt()][it.second.toInt()])
    }.plus(chain.last()).also(::println)
}
