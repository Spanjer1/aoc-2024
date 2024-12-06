package nl.reinspanjer.solvers

import kotlin.math.abs

class Day01 : Solver {
    override fun partOne(input: List<String>): Int {
        val left = mutableListOf<Int>()
        val right = mutableListOf<Int>()
        input.forEach { line ->
            val (a, b) = line.split("   ")
            left.add(a.toInt())
            right.add(b.toInt())
        }
        left.sort()
        right.sort()
        var total = 0
        left.zip(right).forEach { (a, b) ->
            total += abs(a - b)
        }
        return total
    }

    override fun partTwo(input: List<String>): Int {
        val left = mutableListOf<Int>()
        val right = mutableMapOf<Int, Int>()
        input.forEach { line ->
            val (a, b) = line.split("   ")
            val rightInt = b.toInt()
            left.add(a.toInt())
            right[rightInt] = right.getOrDefault(rightInt, 0) + 1
        }
        var total = 0
        left.forEach { a -> total += a * right.getOrDefault(a, 0) }
        return total; }
}