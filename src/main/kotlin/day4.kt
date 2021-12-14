import java.io.File

fun main() {
    val bingoData = File("bingo.txt").readLines().iterator()
    val bingoBalls = bingoData.next().split(",").map(String::toInt)

    val boardRegEx = "\\w*(\\d+)".toRegex()
    val boards = mutableListOf<List<List<Int>>>()
    while (bingoData.hasNext()) {
        bingoData.next() // skip blank line
        boards.add(listOf(
            boardRegEx.findAll(bingoData.next()).toList().map{ it.value.toInt()},
            boardRegEx.findAll(bingoData.next()).toList().map{ it.value.toInt()},
            boardRegEx.findAll(bingoData.next()).toList().map{ it.value.toInt()},
            boardRegEx.findAll(bingoData.next()).toList().map{ it.value.toInt()},
            boardRegEx.findAll(bingoData.next()).toList().map{ it.value.toInt()},
        ))
    }
    println(bingoBalls)
    boards.forEach { board ->
        println()
        println(board[0])
        println(board[1])
        println(board[2])
        println(board[3])
        println(board[4])
    }
}