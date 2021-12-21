import java.io.File
import kotlin.math.pow

class PartialInsertionCalculation constructor(
    private val chain: MutableList<Char>,
    private val rules: Array<CharArray>,
    private val source: PartialInsertionCalculation?
) : Iterator<Char> {
    private var index =
        if (chain[0] == ' ') chain.size else 0 // can't supply values unless there are valid elements in the chain

    override fun hasNext(): Boolean {
        if (!needsRefill())
            return true

        if (canRefill())
            return true

        return index < chain.size
    }

    override fun next(): Char {
        if (needsRefill() && canRefill())
            refillByExpandingNextPair()
        return chain[index++]
    }

    private fun refillByExpandingNextPair() {
        val first = if (chain.last() == ' ') source!!.next() else chain.last()
        val second = source!!.next()
        iterateInsertionRulesIntoChain(chain, first, second, rules)
        index = 0
    }

    private fun canRefill(): Boolean {
        return source != null && source.hasNext()
    }

    private fun needsRefill(): Boolean {
        return index > chain.size - 2 // Use the last element to expand the next portion of the chain
    }
}

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
    val elementFrequencies = chainAfter10.groupingBy { it }.eachCount()
    val sortedFrequencies = elementFrequencies.toList().sortedBy { it.second }.also(::println)
    println(sortedFrequencies.last().second - sortedFrequencies.first().second)

    val insertionCalculationRoot = PartialInsertionCalculation(template.toMutableList(), polymerRules, null)
    val insertionCalculation = PartialInsertionCalculation(
        blankChainToHoldInsertions(10),
        polymerRules,
        PartialInsertionCalculation(
            blankChainToHoldInsertions(10),
            polymerRules,
            PartialInsertionCalculation(
                blankChainToHoldInsertions(10),
                polymerRules,
                PartialInsertionCalculation(blankChainToHoldInsertions(10), polymerRules, insertionCalculationRoot)
            )))
    val elementFrequenciesAt40 = mutableMapOf<Char, Long>()
    insertionCalculation.asSequence().forEach {
        elementFrequenciesAt40[it] = elementFrequenciesAt40.getOrDefault(it, 0)
    }
    val sortedFrequenciesAt40 = elementFrequencies.toList().sortedBy { it.second }.also(::println)
    println(sortedFrequenciesAt40.last().second - sortedFrequenciesAt40.first().second)

}

private fun sizeAtIteration(iterationCount: Int, initialSize: Int): Long {
    val powerOf2 = 2.0.pow(iterationCount.toDouble()).toLong()
    return powerOf2 * initialSize - powerOf2 + 1
}

private fun iterateInsertionRules(first: Char, last: Char, iterations: Int, rules: Array<CharArray>): List<Char> {
    val chain = blankChainToHoldInsertions(iterations)
    iterateInsertionRulesIntoChain(chain, first, last, rules)
    return chain
}

private fun blankChainToHoldInsertions(iterationCount: Int) =
    MutableList(sizeAtIteration(iterationCount, 2).toInt()) { ' ' }

// Calculate the exact final position of each inserted element based on the total number of iterations
// and put it there as soon as it's calculated
private fun iterateInsertionRulesIntoChain(
    chain: MutableList<Char>,
    first: Char,
    last: Char,
    rules: Array<CharArray>
) {
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
}

private fun applyInsertionRules(chain: List<Char>, polymerRules: Array<CharArray>): List<Char> {
    return chain.zipWithNext().flatMap {
        listOf(it.first, polymerRules[it.first.toInt()][it.second.toInt()])
    }.plus(chain.last())
}
