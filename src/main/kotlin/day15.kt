import java.io.File
import kotlin.math.max

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

    val explorer = ChitinExplorer(maxX, maxY) { location: Point -> chitinLevels[location]!! }
    explorer.findBestPath()
}

class ChitinExplorer(
    private val maxX: Int,
    private val maxY: Int,
    private val chitinAtLocation: (Point) -> Int
) {
    private val endPoint = Point(maxX, maxY)
    private val lowestArrivalCost = mutableMapOf<Point, Int>().withDefault { Int.MAX_VALUE }

    fun findBestPath() {
        findBestPathBreadthFirst(Point(0, 0))
    }

    private fun findBestPathBreadthFirst(startingPoint: Point) {
        val searchPoints = mutableListOf(startingPoint)
        lowestArrivalCost[startingPoint] = 0
        var maxSearchListSize = 0
        var iterations =0
        while (searchPoints.size > 0) {
            searchPoints.sortBy { estimatedFinalPathCost(it) }
            searchPoints.addAll(findNeighborsWithLowArrivalCosts(searchPoints.removeFirst()))
            maxSearchListSize = max(maxSearchListSize, searchPoints.size)
            iterations++
        }
        println("max search points: $maxSearchListSize iterations: $iterations arrival costs calculated: ${lowestArrivalCost.size}")
    }

    private fun findNeighborsWithLowArrivalCosts(location: Pair<Int, Int>): Collection<Point> {
        return neighborsOf(location).filter { neighbor ->
            val newArrivalCost = lowestArrivalCost[location]!! + chitinAtLocation(neighbor)
            (lowestArrivalCost.getValue(neighbor) > newArrivalCost).also { updatedCost ->
                if (updatedCost) {
                    lowestArrivalCost[neighbor] = newArrivalCost
                    if (neighbor == endPoint) println(newArrivalCost)
                }
            }
        }
    }

    private fun estimatedFinalPathCost(location: Point): Int {
        return lowestArrivalCost.getValue(location)
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
