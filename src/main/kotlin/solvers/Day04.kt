package nl.reinspanjer.solvers

class Day04 : Solver {
    private val findString = "XMAS"
    private val positions: List<Pair<Int,Int>> = listOf(
        Pair(-1,-1),
        Pair(0,-1),
        Pair(-1,0),
        Pair(1,0),
        Pair(0,1),
        Pair(1,1),
        Pair(-1, 1),
        Pair(1,-1)
    )

    private fun getCharFromCord(matrix: List<List<Char>>, xCord: Int, yCord: Int): Char {
        if (yCord < 0 || yCord >= matrix.size || xCord < 0 || xCord >= matrix[0].size) {
            return ' ' // return char that is not in findString
        }
        return matrix[yCord][xCord]
    }

    private fun getAllSteps(matrix: List<List<Char>>, xCord: Int, yCord: Int, change: Pair<Int,Int>): Boolean {
        if (getCharFromCord(matrix, xCord, yCord) != 'X') {
            return false
        }
        var currentX = xCord
        var currentY = yCord
        for (c in findString) {
            if (getCharFromCord(matrix, currentX, currentY) != c) {
                return false
            }
            currentX += change.first
            currentY += change.second
        }
        return true
    }


    private fun lookAroundPartOne(matrix: List<List<Char>>, xCord: Int, yCord: Int): Int {
        return positions.fold(0) {
            acc, pair ->
            acc + if (getAllSteps(matrix, xCord, yCord, pair )) 1 else 0
        }
    }

    override fun partOne(input: List<String>): Int {
        val (matrix, cords: List<Pair<Int, Int>>) = getMatrixAndCords(input)
        return cords.fold(0){ sum, (x,y) -> sum + lookAroundPartOne(matrix, x,y)}

    }

    private val mPos: List<Pair<Int,Int>> = listOf(
        Pair(-1,-1),
        Pair(1, 1)
    )

    private val sPos: List<Pair<Int,Int>> = listOf(
        Pair(1,-1),
        Pair(-1, 1)
    )

    private fun lookAroundPart2(matrix: List<List<Char>>, xCord: Int, yCord: Int): Boolean {
        val currentChar = getCharFromCord(matrix, xCord, yCord)
        if (currentChar != 'A') {
            return false
        }
        return checkChar(matrix, xCord, yCord, mPos ) && checkChar(matrix, xCord, yCord, sPos )

    }

    private fun checkChar(
        matrix: List<List<Char>>,
        xCord: Int,
        yCord: Int,
        pos: List<Pair<Int,Int>>
    ): Boolean {
        val chars: List<Char> = pos.map{
            (x,y) ->
            val newX = x+xCord
            val newY = y+yCord
            getCharFromCord(matrix, newX, newY)
        }
        return chars.size == 2 && chars.contains('M') && chars.contains('S')
    }

    override fun partTwo(input: List<String>): Int {
        val (matrix, cords: List<Pair<Int, Int>>) = getMatrixAndCords(input)
        return cords.map {
            (x,y) -> lookAroundPart2(matrix,x,y)
        }.count { it }
    }

    private fun getMatrixAndCords(input: List<String>): Pair<MutableList<List<Char>>, List<Pair<Int, Int>>> {
        val matrix = mutableListOf<List<Char>>()
        input.forEach { line ->
            matrix.add(line.toList())
        }
        val xSize = matrix[0].size
        val ySize = matrix.size

        val cords: List<Pair<Int, Int>> = (0..<ySize).map { y ->
            (0..<xSize).map { x ->
                Pair(y, x)
            }
        }.flatten()
        return Pair(matrix, cords)
    }
}