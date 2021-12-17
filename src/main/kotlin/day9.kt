import java.io.File

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val rawHeights = File("lavacave_heightmap.txt").readLines()
    val heightAtPosition = mutableMapOf<Pair<Int, Int>, Int>()
    rawHeights.forEachIndexed { y, rowOfHeights ->
        rowOfHeights.forEachIndexed { x, heightChar ->
            heightAtPosition[Pair(x, y)] = heightChar.digitToInt()
        }
    }

    heightAtPosition.keys.filter { isLowPoint(it, heightAtPosition) }.sumOf{ (heightAtPosition[it]!! + 1)} }
}

private fun min(vararg ints: Int?): Int {
    return ints.fold(Int.MAX_VALUE) { curMin, next ->
        val compVal = next?:Int.MAX_VALUE
        if (compVal < curMin)
            compVal
        else
            curMin
    }
}

private fun isLowPoint(point: Pair<Int, Int>, heightAtPosition: Map<Pair<Int, Int>, Int>): Boolean {
    return heightAtPosition[point]!! < min(
            heightAtPosition[point.copy(first = point.first + 1)],
            heightAtPosition[point.copy(first = point.first - 1)],
            heightAtPosition[point.copy(second = point.second + 1)],
            heightAtPosition[point.copy(second = point.second - 1)],
    )
}