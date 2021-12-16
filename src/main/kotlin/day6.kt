import java.io.File

fun main() {
    var fish = File("fish.txt").readText().split(",").map(String::toInt)

    var fishTanks = LongArray(9)
    fish.forEach { age -> fishTanks[age] = fishTanks[age] + 1}
    (1..80).forEach {ageFish(fishTanks)}
    println(fishTanks.sum())
}

fun ageFish(fishTanks: LongArray) {
    val spawningFishCount = fishTanks[0]
    for(i in 0.rangeTo( fishTanks.size - 2)) { fishTanks[i] = fishTanks[i + 1] }
    fishTanks[fishTanks.size - 1] = spawningFishCount
    fishTanks[6] += spawningFishCount
}

