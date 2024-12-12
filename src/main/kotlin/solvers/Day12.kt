package nl.reinspanjer.solvers

class Day12 : Solver {
    val possiblePos = listOf(
        Pair(-1,0),
        Pair(0, -1),
        Pair(1, 0),
        Pair(0, 1)
    )

    val loc = mapOf(
        "LEFT" to Pair(-1,0),
        "RIGHT" to Pair(1, 0),
        "TOP" to Pair(0, -1),
        "BELOW" to Pair(0, 1)
    )

    override fun partOne(input: List<String>): Number {
        val (matrix: MutableList<MutableList<Cell>>, cells: MutableSet<Cell>) = getInput(input)
        val sets = getAreaSet(cells, matrix)

        var total = 0
        sets.forEach { set ->

            val area = set.size
            var totalMatchingSides = 0
            set.forEach { cell ->
                totalMatchingSides += matchingSides(set, cell)
            }
            val perimeter = area * 4 - totalMatchingSides
            val ans = area * perimeter
            println("$area * $perimeter = $ans")
            total += ans

        }

        return total
    }

    private fun matchingSides(set: Set<Cell>, c: Cell): Int {
        return possiblePos.map {
            val newC = Cell(c.c,  c.x + it.first, c.y + it.second)
            if (set.contains(newC)) {
                1
            } else {
                0
            }
        }.sum()
    }

    private fun region(set: Set<Cell>): Int{
        val sides = mutableMapOf<String, List<Cell>>()
        set.forEach { cell ->
            getLocs(cell).forEach { (key, value) ->
                if (!set.contains(value)) {
                    sides[key] = sides.getOrDefault(key, listOf()).plus(value)
                }
            }
        }

        val groupBy: (String, Cell) -> Pair<String, Int> = {
            s, c -> if (s == "LEFT" || s == "RIGHT") Pair(s, c.x) else Pair(s, c.y)
        }
        val usd: (String, Cell) -> Int = {
                s, c -> if (s == "LEFT" || s == "RIGHT") c.y else c.x
        }
        val mp: MutableMap<Pair<String, Int>, List<Int>> = mutableMapOf()
        sides.forEach{ (key, value) ->
            value.forEach { c ->
                val nKey = groupBy(key, c)
                if (mp.contains(nKey)) {
                    mp[nKey] = mp[nKey]!!.plus(usd(key,c))
                } else {
                    mp[nKey] = listOf(usd(key, c))
                }
            }
        }
        var numberOfSides = 0
        mp.map {  (_, value) ->
            val group = mutableListOf<MutableSet<Int>>()
            value.forEach { v ->
                val fl = group.filter { it.contains(v-1) || it.contains(v+1)}
                if (fl.isNotEmpty()) {
                    fl[0].add(v)
                } else {
                    group.add(mutableSetOf(v))
                }
            }
            numberOfSides += group.size
        }


        return numberOfSides
    }

    private fun getLocs(c: Cell): Map<String, Cell> {
        return loc.map { (key, value) ->
            key to Cell(c.c, c.x + value.first, c.y + value.second)
        }.toMap()
    }


    private fun getFromMatrixOrNull(matrix: List<List<Cell>>, x: Int, y: Int): Cell? {
        if (x < 0 || x >= matrix[0].size || y < 0 || y >= matrix.size) {
            return null
        }
        return matrix[y][x]
    }

    private fun lookAround(matrix: List<List<Cell>>, c: Cell): List<Cell> {
        val cells = possiblePos.mapNotNull { (x, y) ->
            val newX = c.x + x
            val newY = c.y + y
            getFromMatrixOrNull(matrix, newX, newY)
        }.filter { it.c == c.c}
        return cells
    }

    override fun partTwo(input: List<String>): Number {
        val (matrix: MutableList<MutableList<Cell>>, cells: MutableSet<Cell>) = getInput(input)
        val sets = getAreaSet(cells, matrix)
        val answer = sets.sumOf { region(it) * it.size}
        return answer
    }

    private fun getAreaSet(
        cells: MutableSet<Cell>,
        matrix: MutableList<MutableList<Cell>>
    ): Set<MutableSet<Cell>> {
        val areaMap = mutableMapOf<Cell, MutableSet<Cell>>()
        cells.forEach { cell ->
            val neighbours = lookAround(matrix, cell).filter { areaMap.containsKey(it) }

            if (neighbours.isNotEmpty()) {
                val mergedSet = neighbours
                    .mapNotNull { areaMap[it] }
                    .reduce { acc, set ->
                        acc.apply { addAll(set) }
                    }

                mergedSet.add(cell)
                mergedSet.forEach { areaMap[it] = mergedSet }
            } else {
                areaMap[cell] = mutableSetOf(cell)
            }
        }

        return areaMap.values.toSet()
    }

    private fun getInput(input: List<String>): Pair<MutableList<MutableList<Cell>>, MutableSet<Cell>> {
        val matrix: MutableList<MutableList<Cell>> = mutableListOf()
        val cells: MutableSet<Cell> = mutableSetOf()

        input.forEachIndexed { y, s ->
            matrix.add(y, mutableListOf())
            s.forEachIndexed { x, c ->
                val cell = Cell(c, x, y)
                matrix[y].add(x, cell)
                cells.add(cell)
            }
        }
        return Pair(matrix, cells)
    }
}

data class Cell(val c: Char, val x: Int, val y: Int)