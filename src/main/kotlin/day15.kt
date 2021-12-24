import java.io.File

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    val chitinLines = File("cave_chitins.txt").readLines()
    val chitinLevels = mutableMapOf<Point, Int>().withDefault { 0 }
    val maxX = chitinLines[0].length - 1
    val maxY = chitinLines.size - 1
    chitinLines.forEachIndexed { y, levels ->
        levels.forEachIndexed { x, levelAsChar ->
            chitinLevels[Point(x, y)] = levelAsChar.digitToInt()
        }
    }

    val explorer = ChitinExplorer(maxX, maxY, chitinLevels)
    explorer.findBestPath()
    println(explorer.bestPathInfo.second)
}


typealias PathWithCost = Pair<Path, Int>

class ChitinExplorer(
    private val maxX: Int,
    private val maxY: Int,
    private val chitinLevels: LevelMap
) {
    var bestPathInfo = PathWithCost(listOf(), Int.MAX_VALUE)
    private val endPoint = Point(maxX,maxY)
    private val lowestArrivalCost = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }
    private val optimalPathForwardFrom = mutableMapOf<Point, Point>()

    fun findBestPath() {
        findBestPath(Point(0, 0), listOf(), 0)
    }

    private fun findBestPath(
        currentLocation: Point,
        currentPath: Path,
        currentCost: Int
    ) {
        if (currentCost > lowestArrivalCost.getValue(currentLocation)) return
        else lowestArrivalCost[currentLocation] = currentCost
        if (bestPathCost() <= bestPossibleCostFrom(currentCost, currentLocation)) return
        if (currentCost > worstPossibleMinimumCostTo(currentLocation)) return
        val nextPath = currentPath + currentLocation
        if (currentLocation == endPoint) {
            printFirstPointNotOnBestPath(nextPath)
            bestPathInfo = Pair(nextPath, currentCost)
            reportBestPath()
            return
        }

        val neighbors = if (optimalPathForwardFrom.containsKey(currentLocation)) {
            listOf(optimalPathForwardFrom[currentLocation]!!)
        } else {
            neighborsOf(currentLocation).filter { !currentPath.contains(it) }.sortedBy {
                chitinLevels[it]!! + if (it.first < currentLocation.first || it.second < currentLocation.second) 5 else 0
            }
        }
        neighbors.forEach { nextLocation ->
            findBestPath(nextLocation, nextPath, currentCost + chitinLevels[nextLocation]!!)
        }
        if (bestPath().contains(currentLocation)) {
            val currentLocationIndex = bestPath().indexOf(currentLocation)
            optimalPathForwardFrom[currentLocation] = bestPath()[currentLocationIndex + 1]
        }
    }

    private fun printFirstPointNotOnBestPath(nextPath: List<Point>) {
        for (i in (0..nextPath.size)) {
            if (bestPath().size <= i) break
            if (bestPath()[i] != nextPath[i]) {
                println(nextPath[i])
                break
            }
        }
    }

    private fun reportBestPath() {
        print("${bestPathCost()} [")
        bestPath().forEach { print("$it:${chitinLevels[it]}:${lowestArrivalCost[it]}, ") }
        println("]")
    }

    private fun bestPathCost() = bestPathInfo.second

    private fun bestPath() = bestPathInfo.first

    private fun bestPossibleCostFrom(costSoFar: Int, currentLocation: Pair<Int, Int>): Int {
        return costSoFar + (maxX - currentLocation.first) + (maxY - currentLocation.second)
    }

    private fun worstPossibleMinimumCostTo(location: Pair<Int, Int>): Int {
        return (location.first * 9) + (location.second * 9)
    }

    private fun neighborsOf(currentLocation: Point): List<Point> {
        val neighbors = mutableListOf<Point>()
        if (currentLocation.first < maxX) neighbors.add(currentLocation.copy(first = currentLocation.first + 1))
        if (currentLocation.second < maxY) neighbors.add(currentLocation.copy(second = currentLocation.second + 1))
        if (currentLocation.first > 0
            && currentLocation.second > 0
            && currentLocation.second < maxY
        ) neighbors.add(currentLocation.copy(first = currentLocation.first - 1))
        if (currentLocation.second > 0
            && currentLocation.first > 0
            && currentLocation.first < maxX
        ) neighbors.add(currentLocation.copy(second = currentLocation.second - 1))
        return neighbors.toList()
    }
}
