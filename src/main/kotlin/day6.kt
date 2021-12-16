import java.io.File

fun main() {
    var fish = File("fish.txt").readText().split(",").map(String::toInt)
    (1..256).forEach {fish = spawnFish(fish)}
    println(fish.size)
}

private fun spawnFish(fish: List<Int>): List<Int> {
    val newFish = mutableListOf<Int>()
    val agedFish = fish.map { age ->
        if (age == 0) {
            newFish.add(8)
            6
        } else {
            age - 1
        }
    }
    return agedFish + newFish
}