import java.io.File

fun main() {
    val tunnels = readTunnels("cave_map.txt")
    val caves = mapCaves(tunnels)
    traverse(caves, listOf("start"), "end", listOf()).onEach(::println).size.also(::println)
    println("********************************************************************************************************")
    traverseSmallCavesTwice(caves, listOf("start"), "end", listOf()).onEach(::println).size.also(::println)
}

private fun readTunnels(tunnelFile: String): List<List<String>> {
    val tunnelRegEx = "(\\w+)-(\\w+)".toRegex()
    val tunnels = File(tunnelFile).readLines().map {
        val (_, fromCave, toCave) = tunnelRegEx.find(it)!!.groupValues
        listOf(fromCave, toCave)
    }
    return tunnels
}

private fun mapCaves(tunnels: List<List<String>>): Map<String, List<String>> {
    println("Here are the caves and the paths leading between them:")
    val caveNames = tunnels.flatten().toSet()
    return caveNames.associate { cave ->
        cave to tunnels
            .filter { tunnel -> tunnel.contains(cave) }
            .map { it.first { otherCave -> cave != otherCave } }
    }.onEach(::println).also { println() }
}

fun traverse(
    caves: Map<String, List<String>>,
    currentPath: List<String>,
    end: String,
    previousTraversals: List<List<String>>
): List<List<String>> {
    if (currentPath.last() == end) return previousTraversals.append(currentPath)
    val explorableCaves = caves[currentPath.last()]!!.filter { connectedCave ->
        !currentPath.contains(connectedCave) || isLarge(connectedCave)
    }
    return explorableCaves.fold(previousTraversals) { currentTraversals, connectedCave ->
        traverse(caves, currentPath.append(connectedCave), end, currentTraversals)
    }
}

fun traverseSmallCavesTwice(
    caves: Map<String, List<String>>,
    currentPath: List<String>,
    end: String,
    previousTraversals: List<List<String>>
): List<List<String>> {
    if (currentPath.last() == end) return previousTraversals.append(currentPath)
    val explorableCaves = caves[currentPath.last()]!!.filter { connectedCave ->
        isLarge(connectedCave)
                || !currentPath.contains(connectedCave)
                || (
                !containsDuplicateSmallCaves(currentPath)
                        && connectedCave != currentPath.first()
                        && connectedCave != end
                )
    }
    return explorableCaves.fold(previousTraversals) { currentTraversals, connectedCave ->
        traverseSmallCavesTwice(caves, currentPath.append(connectedCave), end, currentTraversals)
    }
}

fun containsDuplicateSmallCaves(path: List<String>): Boolean {
    val smallCavesOnPath = path.filter { !isLarge(it) }
    return smallCavesOnPath.size > smallCavesOnPath.toSet().size
}

fun isLarge(caveName: String): Boolean {
    return caveName.first().isUpperCase()
}

private fun <E> List<E>.append(newElement: E): List<E> {
    return toMutableList().apply { add(newElement) }.toList()
}

