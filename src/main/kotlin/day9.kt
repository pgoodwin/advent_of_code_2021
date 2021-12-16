import jdk.nashorn.internal.objects.NativeMath.min
import java.io.File

fun main() {
    val rawHeights = File("lavacave_heightmap.txt").readLines()
    val xMax = rawHeights.first().length
    val yMax = rawHeights.size
    val heightAtPosition = mutableMapOf<Pair<Int, Int>, Int>()
    rawHeights.forEachIndexed { y, rowOfHeights ->
        rowOfHeights.forEachIndexed { x, heightChar ->
            heightAtPosition[Pair(x,y)] = heightChar.toInt()
        }
    }
}

privat fun isLowPoint(point: Pair<Int, Int>, heightAtPosition: Map<Pair<Int, Int>, Int>): Boolean {
}