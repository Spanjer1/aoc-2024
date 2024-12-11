package nl.reinspanjer.solvers

import java.math.BigInteger
import java.util.*

class Day11 : Solver {
    private val cache = mutableMapOf<Stone, BigInteger>()

    override fun partOne(input: List<String>): Number {
        val times = 25
        return execute(input, times)
    }

    private fun createTwoHalves(b: Stone): List<Stone> {
        var nString = b.value.toString()
        var i = nString.length/2
        var x = nString.substring(0,i).toBigInteger()
        var y = nString.substring(i,nString.length).toBigInteger()
        return listOf(
            Stone(x, b.timesLeft-1),
            Stone(y, b.timesLeft-1)
        )
    }


    fun blinks(stone: Stone): List<Stone> {
        if (stone.value == BigInteger.ZERO) {
            return listOf(Stone(BigInteger.ONE, stone.timesLeft-1))
        } else if (stone.value.toString().length % 2 == 0) {
            return createTwoHalves(stone)
        } else {
            return listOf(Stone(stone.value.times(BigInteger.valueOf(2024)), stone.timesLeft-1))
        }
    }

    fun blink(stone: Stone): BigInteger {
        val stack = Stack<StoneNode>()
        val root = StoneNode(stone, null, null, BigInteger.ONE )
        stack.push(root)
        var total = BigInteger.ZERO
        while (stack.isNotEmpty()) {
            val node: StoneNode = stack.removeLast()
            val current: Stone = node.stone
            if (current.timesLeft == 0) {
                node.finished = true
                node.size = BigInteger.ONE
                node.children = null
                addToCache(node)
                total += node.size
            }
            else if (cache.containsKey(current)) {
                node.size = cache[current]!!
                node.finished = true
                node.children = null
                addToCache(node)
                total += node.size
            }
            else {
                val stones: List<StoneNode> = blinks(current).map{ StoneNode(it, node, null, BigInteger.ONE)}
                node.children = stones
                node.size = stones.size.toBigInteger()
                stones.forEach {
                    stack.push(it)
                }
            }
        }

        return total
    }

    private fun addToCache(node: StoneNode) {
        var current: StoneNode? = node.parent
        while (current != null && checkChildren(current)) {
            current.size = current.children!!.sumOf{it.size}
            cache[current.stone] = current.size
            current.finished = true
            current = current.parent
        }
    }

    private fun checkChildren(stone: StoneNode): Boolean {
        if (stone.children == null) {
            return true
        }
        return stone.children!!.all { it.finished }
    }

    override fun partTwo(input: List<String>): Number {
        val times = 75
        return execute(input, times)
    }

    private fun execute(input: List<String>, times: Int): BigInteger {
        val inputBigInt: List<BigInteger> = input[0].split(" ").map { it.toBigInteger() }
        return inputBigInt.fold(BigInteger.ZERO) { acc, b ->
            acc + blink(Stone(b, times))
        }
    }
}

data class StoneNode(val stone: Stone, val parent: StoneNode?,  var children: List<StoneNode>?, var size: BigInteger, var finished: Boolean = false)

data class Stone(var value: BigInteger, var timesLeft: Int): Comparable<Stone> {
    override fun compareTo(other: Stone): Int {
       return  if (this.value == other.value && this.timesLeft == other.timesLeft) 0 else 1
    }
}