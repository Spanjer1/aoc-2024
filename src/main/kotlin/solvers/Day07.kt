package nl.reinspanjer.solvers

import java.math.BigInteger
import kotlin.math.pow

class Day07 : Solver {

    override fun partOne(input: List<String>): BigInteger {
        return solveWithOperators(input, listOf("*", "+"))
    }

    private fun solveWithOperators(
        input: List<String>,
        operators: List<String>
    ): BigInteger {
        val cache = mutableMapOf<Int, List<List<String>>>()
        var total: BigInteger = BigInteger.ZERO
        input.forEach { line ->
            val l = line.split(": ")
            val answer = l[0].toBigInteger()
            val numbers = l[1].split(' ').map { it.toBigInteger() }

            if (!cache.containsKey(numbers.size)) {
                cache[numbers.size] = getAllCombinations(operators, numbers.size - 1)
            }
            if (checkIfPossible(answer, numbers, cache[numbers.size]!!)) {
                total += answer
            }

        }
        return total;
    }

    private fun checkIfPossible(answer: BigInteger, numbers: List<BigInteger>, combinations: List<List<String>>): Boolean {

        return combinations.map{ combination ->
            val res = numbers.drop(1)
                .foldIndexed(numbers[0]){ index, acc, number ->
                execute(combination[index], acc, number) }

            res == answer
        }.any { it }
    }

    private fun execute(operation: String, x: BigInteger, y: BigInteger): BigInteger {
        return when (operation) {
            "+" -> {
                x + y
            }
            "*" -> {
                x * y
            }
            "||" -> {
                (x.toString() + y.toString()).toBigInteger()
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }


    private fun getAllCombinations(operators: List<String>, n: Int): List<List<String>> {
        val sizeOfSeq = operators.size.toDouble().pow(n).toInt()
        return (0 until sizeOfSeq).map { i ->
            (0 until n).map { j ->
                val index = i / operators.size.toDouble().pow(j).toInt() % operators.size
                operators[index]
            }
        }
    }

    override fun partTwo(input: List<String>): BigInteger {
        return solveWithOperators(input, listOf("*", "+", "||"))
    }
}