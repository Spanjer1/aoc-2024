package nl.reinspanjer.solvers

class Day08: Solver {
    override fun partOne(input: List<String>): Number {
        val maxX = input[0].length
        val maxY = input.size

        val antennaMap = getAntennaMap(input)
        val mSet = mutableSetOf<Pair<Int,Int>>()
        antennaMap.forEach{ (key, value) ->
            mSet.addAll(calculateAntinodes(antennaMap, value))
        }

        return  mSet.fold(0) { acc, pair ->
            if (pair.first in 0..<maxX && pair.second in 0..<maxY ) {
                acc + 1
            } else {
                acc
            }
        }
    }

    private fun getAntennaMap(input: List<String>): MutableMap<Pair<Int, Int>, Antenna> {
        val antennaMap = mutableMapOf<Pair<Int, Int>, Antenna>()

        input.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c != '.') {
                    antennaMap[Pair(x, y)] = Antenna(c, Pair(x, y))
                }
            }
        }
        return antennaMap
    }

    private fun calculateAntinodes(antennaMap: Map<Pair<Int,Int>, Antenna>, antenna: Antenna): Set<Pair<Int,Int>> {
        val allCords = antennaMap.filterValues { it.c == antenna.c }.keys
        val (aX, aY) = antenna.cords
        val mSet = mutableSetOf<Pair<Int,Int>>()
        allCords.map { (x,y) ->
            val distance = Pair(x-aX,y-aY)
            Pair(
                Pair(aX - distance.first, aY - distance.second),
                Pair(x + distance.first,y + distance.second)
            )
        }.forEach {
            (p1, p2) ->
            if (p1 != p2) {
                mSet.add(p1)
                mSet.add(p2)
            }
        }

        return mSet
    }

    private fun calculateAntinodesWithT(antennaMap: Map<Pair<Int,Int>, Antenna>, antenna: Antenna, range: Pair<Int,Int>): Set<Pair<Int,Int>> {
        val allCords = antennaMap.filterValues { it.c == antenna.c }.keys
        val (aX, aY) = antenna.cords
        return allCords.map { (x,y) ->
            val distance = Pair(x-aX,y-aY)
            if (distance != Pair(0,0)) {
                createDistanceList(aX,aY, distance, range) + createDistanceList(x,y, Pair(distance.first*-1, distance.second*-1), range)
            } else {
                listOf()
            }

        }.flatten().toSet()
    }

    private fun createDistanceList(
        aX: Int,
        aY: Int,
        distance: Pair<Int, Int>,
        range: Pair<Int, Int>
    ): List<Pair<Int, Int>> {

        return generateSequence(Pair(aX - distance.first, aY - distance.second)) {
            Pair(it.first - distance.first, it.second - distance.second)
        }.takeWhile {
            it.first in 0 until range.first && it.second in 0 until range.second
        }.toList()
    }

    override fun partTwo(input: List<String>): Number {
        val range = Pair(input[0].length, input.size)

        val antennaMap = getAntennaMap(input)
        val antiNodes = antennaMap.map{ (_, value) -> calculateAntinodesWithT(antennaMap, value, range)}.flatten().toSet()

        return (antennaMap.keys + antiNodes).size
    }

    private fun printGrid(range: Pair<Int, Int>, antennaMap: Map<Pair<Int,Int>, Antenna>, antiNodes: Set<Pair<Int,Int>>) {
        (0..<range.second).forEach { y ->
            (0..<range.first).forEach {x ->
                if (antennaMap.containsKey(Pair(x,y))) {
                    print(antennaMap[Pair(x,y)]?.c)
                } else if (antiNodes.contains(Pair(x,y))) {
                    print('#')
                } else {
                    print('.')
                }
            }
            print('\n')
        }
    }
}

data class Antenna(val c: Char, val cords: Pair<Int,Int>)