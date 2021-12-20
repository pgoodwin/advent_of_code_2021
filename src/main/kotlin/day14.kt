import java.io.File
import kotlin.math.pow

fun main() {
    val polymerFileLines = File("polymer_rules.txt").readLines()
    val template = polymerFileLines.first().toList()

    val polymerRuleRegEx = "(\\w\\w) -> (\\w)".toRegex()
    val polymerRulesMap = polymerFileLines.drop(2).map {
        val (_, elementPair, insertionValue) = polymerRuleRegEx.find(it)!!.groupValues
        Pair(elementPair.toList(), insertionValue.toList().single())
    }.toMap()
    val maxElementIndex = polymerRulesMap.keys.flatten().maxOf { it } + 1
    val polymerRules = Array(maxElementIndex.toInt()) { CharArray(maxElementIndex.toInt()) { ' ' } }
    polymerRulesMap.forEach {
        polymerRules[it.key.first().toInt()][it.key.last().toInt()] = it.value
    }

    val chainAfter10 = (1..10).fold(template) { chain, _ ->
        applyInsertionRules(chain, polymerRules)
    }.also(::println)
    println(iterateInsertionRules(template[0], template[1], 10, polymerRules))
    val elementFrequencies = chainAfter10.groupingBy { it }.eachCount()
    val sortedFrequencies = elementFrequencies.toList().sortedBy { it.second }.also(::println)
    println(sortedFrequencies.last().second - sortedFrequencies.first().second)
}

private fun sizeAtIteration(iterationCount: Int, initialSize: Int): Long {
    val powerOf2 = 2.0.pow(iterationCount.toDouble()).toLong()
    return powerOf2 * initialSize - powerOf2 + 1
}

private fun iterateInsertionRules(first: Char, last: Char, iterations: Int, rules: Array<CharArray>): List<Char> {
    val chain = MutableList(sizeAtIteration(iterations, 2).toInt()) { ' ' }
    return iterateInsertionRulesIntoChain(chain, first, last, rules)
}

// Calculate the exact final position of each inserted element based on the total number of iterations
// and put it there as soon as it's calculated
private fun iterateInsertionRulesIntoChain(
    chain: MutableList<Char>,
    first: Char,
    last: Char,
    rules: Array<CharArray>
): MutableList<Char> {
    val lastIndex = chain.size - 1

    chain[0] = first
    chain[lastIndex] = last

    var step = lastIndex
    while (step > 1) {
        var index = step
        while (index <= lastIndex) {
            val charAtStart = chain[index - step].toInt()
            val charAtEnd = chain[index].toInt()
            val centerOfInterval = index - (step / 2)
            chain[centerOfInterval] = rules[charAtStart][charAtEnd]
            index += step
        }
        step /= 2
    }
    return chain
}

private fun applyInsertionRules(chain: List<Char>, polymerRules: Array<CharArray>): List<Char> {
    return chain.zipWithNext().flatMap {
        listOf(it.first, polymerRules[it.first.toInt()][it.second.toInt()])
    }.plus(chain.last())
}
