package nl.reinspanjer.solvers

class StateMachine(part: Int) {
    private var currentState: Int = 0
    val validMultiplications = mutableListOf<Pair<Int,Int>>()
    private val numberList = mutableListOf<Int>()
    private var currentNumberString = ""
    private var standardPartTwo = mapOf('m' to 1, 'd' to 5)
    private var standardPartOne = mapOf('m' to 1)
    private var enabled = true

    private var stateMapPart2 = mutableMapOf<Int, (Char) -> Unit>(
        0 to curryState(0, standardPartTwo) ,
        1 to curryState(0, standardPartTwo + mapOf('u' to 2)),
        2 to curryState(0, standardPartTwo + mapOf('l' to 3)),
        3 to curryState(0, standardPartTwo + mapOf('(' to 4)),
        4 to ::numberState,
        5 to curryState(0, standardPartTwo + mapOf('o' to 6)),
        6 to curryState(0, standardPartTwo + mapOf('(' to 7, 'n' to 9)),
        7 to curryState(0, standardPartTwo + mapOf(')' to 8)),
        8 to {c ->
            enabled = true
            curryState(0, standardPartTwo)(c)
        },
        9 to curryState(0, standardPartTwo + mapOf('\'' to 10)),
        10 to curryState(0, standardPartTwo + mapOf('t' to 11)),
        11 to {c ->
            enabled = false
            curryState(0, standardPartTwo)(c)
        },
    )

    private var stateMapPart1 = mutableMapOf<Int, (Char) -> Unit>(
        0 to curryState(0, standardPartOne) ,
        1 to curryState(0, standardPartOne + mapOf('u' to 2)),
        2 to curryState(0, standardPartOne + mapOf('l' to 3)),
        3 to curryState(0, standardPartOne + mapOf('(' to 4)),
        4 to ::numberState
    )

    private var stateMap: Map<Int, (Char) -> Unit> = if (part == 2) {
        stateMapPart2
    } else {
        stateMapPart1
    }

    fun execute(char: Char) {
        stateMap[currentState]!!.invoke(char)
    }

    fun clearWith(state: Int) {
        currentState = state
        currentNumberString = ""
        numberList.clear()
    }

    fun numberState(char: Char) {
        if (char.isDigit()) {
            currentNumberString += char
        } else if (char == ',' ) {
            numberList.add(currentNumberString.toInt())
            currentNumberString = ""
        } else if (char == ')') {
            numberList.add(currentNumberString.toInt())
            if (numberList.size == 2 && enabled) {
                validMultiplications.add(Pair(numberList[0], numberList[1]))
            }
            clearWith(0)
        } else {
            if (char == 'm') {
                clearWith(1)
            } else {
                clearWith(0);
            }

        }
    }

    fun curryState(default: Int, m: Map<Char, Int>): (Char) -> (Unit) = { char: Char ->
            currentState = m.getOrDefault(char, default)
    }

}
class Day03 : Solver {

    override fun partOne(input: List<String>): Int {
        val inputString = input.joinToString("")
        val sm = StateMachine(1)

        inputString.toList().forEach(
            sm::execute
        )
        println(sm.validMultiplications)
        println(sm.validMultiplications.size)

        return sm.validMultiplications.fold(0) { acc, (a,b) ->
            val add = a * b
            println("$acc, $a $b = $add")
            acc + add
        }
    }

    override fun partTwo(input: List<String>): Int {
        val inputString = input.joinToString("")
        val sm = StateMachine(2)

        inputString.toList().forEach(
            sm::execute
        )

        println(sm.validMultiplications)
        println(sm.validMultiplications.size)

        return sm.validMultiplications.fold(0) { acc, (a,b) ->
            val add = a * b
            println("$acc, $a $b = $add")
            acc + add
        }
    }
}