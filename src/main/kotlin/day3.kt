import java.io.File


fun main() {
    val diagnostics = File("diagnostic_data.txt").readLines().map(String::toCharArray)
    val width = diagnostics[0].size
    val histogram = Array<MutableMap<Char, Int>>(width) { mutableMapOf() }
    diagnostics.forEach { readings ->
        readings.forEachIndexed { i, reading ->
            histogram[i][reading] = if (histogram[i][reading] is Int)
                histogram[i][reading]!! + 1
            else
                1
        }
    }

    var gamma = 0
    var epsilon = 0
    val mostFrequentValueAtPosition = CharArray(width) { ' ' }
    histogram.forEachIndexed { i, entry ->
        gamma *= 2
        epsilon *= 2
        if (entry['1']!! > entry['0']!!) {
            gamma += 1
            mostFrequentValueAtPosition[i] = '1'
        } else {
            epsilon += 1
            if (entry['1']!! < entry['0']!!)
                mostFrequentValueAtPosition[i] = '0'
        }
    }

    println(gamma)
    println(epsilon)
    println(gamma * epsilon)

    var candidateOxygenReadings = diagnostics
    var candidateCo2Readings = diagnostics
    for(i in 0..width) {
        val oxygenReading = if (mostFrequentValueAtPosition[i] == '0') '0' else '1'
        val co2Reading = if (mostFrequentValueAtPosition[i] == '0') '1' else '0'
        if (candidateOxygenReadings.size > 1) candidateOxygenReadings = candidateOxygenReadings.filter { reading -> reading[i] == oxygenReading }
        if (candidateCo2Readings.size > 1) candidateCo2Readings = candidateCo2Readings.filter { reading -> reading[i] == co2Reading }
        if (candidateOxygenReadings.size == 1 && candidateCo2Readings.size == 1)
            break
    }

    val oxygenReadingAsInt = readingToInt(candidateOxygenReadings.first())
    val co2ReadingAsInt = readingToInt(candidateCo2Readings.first())

    println(oxygenReadingAsInt)
    println(co2ReadingAsInt)
    println(oxygenReadingAsInt * co2ReadingAsInt)
}

private fun readingToInt(reading: CharArray): Int {
    var readingAsInt = 0
    reading.forEach { bit ->
        readingAsInt *= 2
        if (bit == '1')
            readingAsInt++
    }
    return readingAsInt
}


