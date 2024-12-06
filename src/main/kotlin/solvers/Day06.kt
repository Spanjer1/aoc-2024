package nl.reinspanjer.solvers

class Day06 : Solver {

    override fun partOne(input: List<String>): Int {
        val guardMap = getGuardMap(input)
        printGuardMap(guardMap)
        while (guardMap.move()) {
        }
        printGuardMap(guardMap)
        return guardMap.getObjectSize()
    }

    private fun getGuardMap(input: List<String>): GuardMap {
        val maxY = input.size
        val maxX = input[0].length
        val objects = mutableSetOf<Pair<Int, Int>>()
        var pos: Pair<Int, Int> = Pair(-1, -1)
        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, char ->
                if (char == '#') objects.add(Pair(x, y))
                else if (char == '^') pos = Pair(x, y)
            }
        }

        check(pos.first != -1 && pos.second != -1)

        val guardMap = GuardMap(objects, pos, maxX, maxY)
        return guardMap
    }

    override fun partTwo(input: List<String>): Int {
        val guardMap = getGuardMap(input)
        return (0..<guardMap.maxY)
            .asSequence()
            .flatMap { y -> (0..<guardMap.maxX).map { x -> Pair(x, y) } }
            .count { pos ->
                guardMap.reset()
                guardMap.obstruction = pos
                try {
                    while (guardMap.move()) {
                    }
                    false
                } catch (e: LoopException) {
                    true
                }
            }

    }
}

val directionToChar = mapOf(
    DIRECTION.UP to '^',
    DIRECTION.DOWN to 'v',
    DIRECTION.RIGHT to '>',
    DIRECTION.LEFT to '<'
)

fun printGuardMap(guardMap: GuardMap) {
    val colors = mapOf(
        "red" to "\u001b[31m",
        "green" to "\u001b[32m",
        "blue" to "\u001b[34m",
        "reset" to "\u001b[0m"
    )

    fun colorize(char: Char, color: String) = "${colors[color]}$char${colors["reset"]}"

    (0..<guardMap.maxY).forEach { y ->
        (0..<guardMap.maxX).joinToString("") { x ->
            val p = Pair(x, y)
            when {
                guardMap.objects.contains(p) -> "#"
                p == guardMap.pos -> colorize(directionToChar[guardMap.direction]!!, "red")
                guardMap.path.contains(p) -> colorize('X', "blue")
                p == guardMap.obstruction -> colorize('O', "green")
                else -> "."
            }
        }.let(::println)
    }
}

enum class DIRECTION(private val pair: Pair<Int, Int>) {
    UP(Pair(0, -1)),
    DOWN(Pair(0, 1)),
    LEFT(Pair(-1, 0)),
    RIGHT(Pair(1, 0));

    fun getPair(): Pair<Int, Int> = pair
}

class LoopException(message: String) : Exception(message)

class GuardMap(val objects: Set<Pair<Int, Int>>, var pos: Pair<Int, Int>, var maxX: Int, var maxY: Int) {
    private val originalPos = pos
    val path = mutableSetOf<Pair<Int, Int>>()
    var obstruction: Pair<Int, Int> = Pair(-1, -1)
    private val pathWithDir = mutableSetOf<Pair<Pair<Int, Int>, DIRECTION>>()
    var direction = DIRECTION.UP
    private val turnDirection = mapOf(
        DIRECTION.UP to DIRECTION.RIGHT,
        DIRECTION.RIGHT to DIRECTION.DOWN,
        DIRECTION.DOWN to DIRECTION.LEFT,
        DIRECTION.LEFT to DIRECTION.UP,
    )

    fun move(): Boolean {
        var newCoordinate = getNewCoordinate(direction, pos)

        if (objects.contains(newCoordinate) || obstruction == newCoordinate) {
            direction = turnDirection[direction]!!
            return move()
        }

        path.add(pos)
        val pathDir = Pair(pos, direction)
        if (pathWithDir.contains(pathDir)) {
            throw LoopException("Loop occurred")
        }
        pathWithDir.add(pathDir)


        pos = newCoordinate
        if (newCoordinate.first >= maxX || newCoordinate.second >= maxY || newCoordinate.first < 0 || newCoordinate.second < 0) {
            return false
        }
        return true
    }


    private fun getNewCoordinate(dir: DIRECTION, cPos: Pair<Int, Int>): Pair<Int, Int> {

        val newX = cPos.first + dir.getPair().first
        val newY = cPos.second + dir.getPair().second
        return Pair(newX, newY)
    }

    fun getObjectSize(): Int {
        return path.size
    }

    fun reset() {
        path.clear()
        pathWithDir.clear()
        obstruction = Pair(-1, -1)
        pos = originalPos
        direction = DIRECTION.UP
    }

}