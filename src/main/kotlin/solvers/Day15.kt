package nl.reinspanjer.solvers

operator fun Pair<Int, Int>.plus(it: Pair<Int,Int>): Pair<Int,Int> {
    return Pair(first + it.first, second + it.second)
}

class Day15 : Solver {

    val charDirMap = mapOf(
        '^' to Pair(-1, 0),
        '<' to Pair(0, -1),
        '>' to Pair(0, 1),
        'v' to Pair(1, 0)
    )

    override fun partOne(input: List<String>): Number {
        val indexOfSplit = input.indexOf("")
        val grid = input.subList(0, indexOfSplit)
        val directions = input.subList(indexOfSplit+1, input.size).joinToString("")
        val robotGrid = getRobotGrid(grid)

        directions.forEach {
            println("Moving $it")

            val dir = charDirMap[it]
            val newLoc = robotGrid.pos + dir!!
            val c = robotGrid.getCharAt(newLoc)

            if (c == '.') {
                robotGrid.moveRobot(newLoc)
            } else if (  c == 'O' ) {
                robotGrid.shiftToNextDot(newLoc, dir)

            }

        }

        return robotGrid.getScore('O')
    }

    override fun partTwo(input: List<String>): Number {
        val indexOfSplit = input.indexOf("")
        // replace complete rows
        val grid = input.subList(0, indexOfSplit).map{
            val s = StringBuilder()
            it.forEach {
                c -> if (c == '#') {
                        s.append("##")
                    } else if (c == 'O') {
                        s.append("[]")
                    } else if (c == '.') {
                        s.append("..")
                    } else if ( c == '@') {
                        s.append("@.")
                    }
            }
            s.toString()
        }

        val directions = input.subList(indexOfSplit+1, input.size).joinToString("")
        val robotGrid = getRobotGrid(grid)

        directions.forEach {
            println("Moving $it")

            val dir = charDirMap[it]
            val newLoc = robotGrid.pos + dir!!
            val c = robotGrid.getCharAt(newLoc)

            if (c == '.') {
                robotGrid.moveRobot(newLoc)
            } else if (  c == '[' || c == ']' ) {
                robotGrid.shiftToNextDotPart2(newLoc, dir)
            }

        }

        return robotGrid.getScore('[')
    }

    private fun getRobotGrid(grid: List<String>): RobotGrid {
        var robotLoc: Pair<Int, Int> = 0 to 0
        val robotGrid = mutableMapOf<Pair<Int, Int>, Char>()
        var maxRow = 1
        var maxColumn = 1

        grid.forEachIndexed { row, s ->
            s.forEachIndexed { col, c ->

                robotGrid[Pair(row, col)] = c
                if (c == '@') {
                    robotLoc = row to col
                }
                if (col > maxColumn) {
                    maxColumn = col
                }
            }

            if (row > maxRow) {
                maxRow = row
            }
        }
        return RobotGrid(robotLoc, robotGrid, Pair(maxRow, maxColumn))
    }
}

data class RobotGrid(var pos: Pair<Int,Int>, val grid: MutableMap<Pair<Int,Int>,Char>, val dimensions: Pair<Int,Int>) {

    fun getScore(c: Char): Int {
        var total = 0
        (0..<dimensions.first).forEach { row ->
            (0..<dimensions.second).forEach { col ->
                if (grid[Pair(row, col)] == c) {
                    total += row * 100 + col
                }
            }
        }
        return total
    }

    fun printGrid() {
        (0..dimensions.first).forEach{ row ->
            (0..dimensions.second).forEach{ column ->
                print(grid[row to column])
            }
            println()
        }
    }

    fun getCharAt(pos: Pair<Int,Int>): Char {
        return grid[pos]!!
    }

    fun moveRobot(newPos: Pair<Int,Int>) {
        grid[pos] = '.'
        grid[newPos] = '@'
        pos = newPos
    }

    private fun getGroupUpDown(acc: List<Pair<Int,Int>>, check: List<Pair<Int,Int>>, dir: Pair<Int,Int>): Pair<Boolean, List<Pair<Int,Int>>> {
        val allPos = check.map { it + dir }
        if (allPos.all { grid[it] == '.' }) {
            return true to acc
        } else if (allPos.any { grid[it] == '#' }) {
            return false to acc
        }
        val newPos = allPos.filter{ grid[it] == '[' || grid[it] == ']' }.map {
            if (grid[it] == ']') {
                listOf(it, it + Pair(0,-1))
            } else if (grid[it] == '[') {
                listOf(it, it + Pair(0,1))
            } else {
                listOf(it)
            }
        }.flatten().toSet().toList()
        val newAcc = acc.plus(newPos)
        return getGroupUpDown(newAcc, newPos, dir)
    }

    private fun getGroupLeftRight(newPos: Pair<Int,Int>, dir: Pair<Int,Int>): Pair<Boolean, List<Pair<Int,Int>>>  {
        val seq = generateSequence(newPos) { it + dir }.takeWhile { grid[it] == '[' || grid[it] == ']' }
        return if (grid[seq.last() + dir] == '.') {
            true to seq.toList()
        } else {
            false to seq.toList()
        }
    }

    fun shiftToNextDotPart2(newPos: Pair<Int,Int>, dir: Pair<Int,Int>) {
        val l = mutableListOf(newPos)
        if (grid[newPos] == ']') {
            l.add(newPos + Pair(0,-1))
        } else if (grid[newPos] == '[') {
            l.add(newPos + Pair(0,1))
        }
        val (b, pos) = if (dir.first != 0) {
            getGroupUpDown(l, l, dir)
        } else {
            getGroupLeftRight(newPos, dir)
        }

        if (b) {
            pos.asReversed().forEach{
                val v = grid[it]
                grid[it + dir] = v!!
                grid[it] = '.'
            }
            moveRobot(newPos)
        }

    }

    fun shiftToNextDot(newPos: Pair<Int,Int>, dir: Pair<Int,Int>) {
        val seq = generateSequence(pos) { it + dir }
        val it = seq.iterator()
        var v = it.next()
        while (grid[v] != '.' && grid[v] != '#') {
            v = it.next()
        }

        val p = if (grid[v] == '.' ) {
            v
        } else {
            null
        }

        if (p != null) {
            grid[p] = 'O'
            grid[newPos] = '@'
            grid[pos] = '.'
            pos = newPos
        }
    }
}