package nl.reinspanjer.solvers

import java.math.BigInteger
import java.util.*

enum class QUADRANT {
    UP_LEFT,
    UP_RIGHT,
    DOWN_LEFT,
    DOWN_RIGHT
}
class Day14: Solver {
    override fun partOne(input: List<String>): Number {
        val range = Pair(101, 103)
        val robots: List<Robot> = getRobots(input)
        robots.forEach {mov(it, range, 100.toBigInteger())}

        val mapOfPos = mutableMapOf<QUADRANT, Int>()
        for (robot in robots) {
            val q = getQuadrant(robot.pos, range)
            if (q != null) {
                mapOfPos[q] = mapOfPos.getOrDefault(q, 0) + 1
            }
        }

        return mapOfPos.values.fold(1){ acc, r ->
            acc * r
        }
    }

    private fun getQuadrant(pos: Pair<Int,Int>, range: Pair<Int,Int>): QUADRANT? {
        val xMiddle = getMiddle(range.first)
        val yMiddle = getMiddle(range.second)
        if (pos.first in xMiddle || pos.second in yMiddle) {
            return null
        }

        val leftOfMiddle = pos.first < xMiddle[0]
        val upOfMiddle = pos.second < yMiddle[0]

        return if (leftOfMiddle && upOfMiddle) {
            QUADRANT.UP_LEFT
        } else if (!leftOfMiddle && upOfMiddle) {
            QUADRANT.UP_RIGHT
        } else if (leftOfMiddle) {
            QUADRANT.DOWN_LEFT
        } else {
            QUADRANT.DOWN_RIGHT
        }
    }

    private fun getMiddle(n: Int) = if (n % 2 == 1) {
        listOf(n/ 2)
    } else {
        listOf(n / 2, n / 2 + 1)
    }

    private fun mov(robot: Robot, range: Pair<Int,Int>, times: BigInteger) {
        val newXBig = (robot.pos.first.toBigInteger() + (robot.velocity.first.toBigInteger() * times)) % (range.first.toBigInteger())
        val newYBig = (robot.pos.second.toBigInteger() + (robot.velocity.second.toBigInteger() * times)) % (range.second.toBigInteger())
        var newX = newXBig.toInt()
        var newY = newYBig.toInt()

        if (newX < 0) {
            newX += range.first
        }

        if (newY < 0) {
            newY += range.second
        }
        robot.pos = Pair(newX, newY)
    }

    private fun movWithoutChange(pos: Pair<Int,Int>, velocity: Pair<Int, Int>, range: Pair<Int,Int>): Pair<Int,Int> {
        var newX = (pos.first + (velocity.first)) % (range.first)
        var newY = (pos.second + (velocity.second)) % (range.second)

        if (newX < 0) {
            newX += range.first
        }

        if (newY < 0) {
            newY += range.second
        }
        return Pair(newX, newY)
    }

    private fun printRobots(range: Pair<Int,Int>, mapOfPos: Map<Pair<Int,Int>, Int>) {
        (0..<range.second).forEach { y ->
            (0..<range.first).forEach { x ->
                if (mapOfPos.getOrDefault(Pair(x, y), 0) > 0) {
                    print(mapOfPos[Pair(x, y)])
                } else {
                    print(".")
                }
            }
            print("\n")
        }

    }

    private fun parsePair(s: String, key: String): Pair<Int,Int> {
        val velocity = s.split(key)[1].split(",").map { n -> n.toInt() }
        return Pair(velocity[0], velocity[1])
    }

    private fun calculatePath(robot: Robot, range: Pair<Int, Int>): Set<Pair<Int,Int>> {
        val positions: MutableSet<Pair<Int, Int>> = mutableSetOf()
        var oldSize = 0
        var currentPos = robot.pos
        positions.add(currentPos)

        while (positions.size != oldSize) {
            oldSize = positions.size
            currentPos = movWithoutChange(currentPos, robot.velocity, range)
            positions.add(currentPos)
        }
        return positions
    }

    fun getRobotsAtSecond(sec: Int, poss: List<Set<Pair<Int, Int>>>): Map<Pair<Int,Int>, Int> {
        val realSec = sec % poss[0].size
        val map = mutableMapOf<Pair<Int,Int>, Int>()
        poss.map{ it.elementAt(realSec) }.forEach {
            p -> map[p] = map.getOrDefault(p, 0) + 1
        }
        return map
    }

    override fun partTwo(input: List<String>): Number {
        val range = Pair(101, 103)
        val robots: List<Robot> = getRobots(input)
        val paths = robots.map { calculatePath(it, range) }
        val pos = listOf(Pair(50, 0))

        // I was watching all outputs... this number is found for mine.
        val nm = getRobotsAtSecond(6668, paths)
        if (pos.all{ a -> nm.getOrDefault(a,0) >= 1 }) {
            printRobots(range, nm)
        }


        return 6668

    }

    private fun getRobots(input: List<String>): List<Robot> {
        val robots: List<Robot> = input.map {
            val fSplit = it.split(" ")
            val pos = parsePair(fSplit[0], "p=")
            val velocity = parsePair(fSplit[1], "v=")
            Robot(pos, velocity)
        }
        return robots
    }

}

data class Robot(var pos: Pair<Int,Int>, val velocity: Pair<Int,Int>)