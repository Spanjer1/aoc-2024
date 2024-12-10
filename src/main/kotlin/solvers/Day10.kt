package nl.reinspanjer.solvers

class Day10 : Solver {
    val possiblePos = listOf(Pair(-1,0),
                        Pair(0, -1),
                        Pair(1, 0),
                        Pair(0, 1))

    override fun partOne(input: List<String>): Number {
        val matrix = input.map{ l -> l.map{ it.toString().toInt() } }
        var total = 0
        matrix.forEachIndexed{ y, l -> l.forEachIndexed{ x, n ->
            var paths = setOf<Pair<Int,Int>>()
            if (n == 0) {
                paths = paths.plus(findPath(matrix, Pair(x,y), n))
                total += paths.size
            }
        }}
        return total
    }

    private fun findPath(matrix: List<List<Int>>, pos: Pair<Int,Int>, value: Int): List<Pair<Int,Int>> {
        println("$pos value: $value")
        if ( value == 9) {
            return listOf(pos)
        }

        var lst = listOf<Pair<Int,Int>>()

        possiblePos.forEach { p ->
            val newPos = Pair(p.first + pos.first, p.second + pos.second)
            val v = getValue(matrix, newPos)
            if (v == value + 1) {
                lst = lst.plus(findPath(matrix, newPos, v))
            }
        }
        return lst
    }

    private fun findPath(matrix: List<List<Int>>, pos: Pair<Int,Int>, acc: List<Pair<Int,Int>> ): List<List<Pair<Int,Int>>> {
        val value = getValue(matrix, pos)
        if ( value == 9) {
            return listOf(acc)
        }

        return possiblePos.map {
            val newPos = Pair(it.first + pos.first, it.second + pos.second)
            val v = getValue(matrix, newPos)
            if (v == value + 1) {
                findPath(matrix, newPos, acc.plus(newPos))
            } else {
                listOf()
            }
        }.flatten()
    }

    fun getValue(matrix: List<List<Int>>, pos: Pair<Int,Int>): Int {
        if (pos.first < 0 || pos.second < 0 || pos.first >= matrix[0].size || pos.second >= matrix.size) {
            return -1
        }
        return matrix[pos.second][pos.first]
    }


    override fun partTwo(input: List<String>): Number {
        val matrix = input.map{ l -> l.map{ it.toString().toInt() } }
        var total = 0
        matrix.forEachIndexed{ y, l -> l.forEachIndexed{ x, n ->
            if (n == 0) {
                val p  = findPath(matrix, Pair(x,y), listOf())
                total += p.size
            }
        }}
        return total
    }
}