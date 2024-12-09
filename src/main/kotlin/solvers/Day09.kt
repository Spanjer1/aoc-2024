package nl.reinspanjer.solvers

import java.util.*

class Day09 : Solver {
    override fun partOne(input: List<String>): Number {
        val dm = getDataMap(input)
        while (dm.defragSingle()) {
        }
        return dm.mutL.takeWhile { it != -1 }.foldIndexed(0L) { index, acc, value ->
            acc + (value * index)
        }
    }

    fun getDataMap(input: List<String>): DataMap {
        val diskMap = input[0]
        val chunked = diskMap.chunkedSequence(2)
        val dm = DataMap()
        chunked.forEach {
            dm.addBlock(it[0].toString().toInt())
            if (it.length == 2) {
                dm.addFreeSpace(it[1].toString().toInt())
            }
        }
        return dm
    }

    override fun partTwo(input: List<String>): Number {
        val dm = getDataMap(input)
        dm.defragBlock()
        return dm.mutL.foldIndexed(0L) { index, acc, value ->
            if (value == -1) acc else acc + (value * index)
        }
    }
}

// mutable pair
data class FreeRange(var start: Int, var end: Int): Comparable<FreeRange> {
    override fun compareTo(other: FreeRange) = compareValuesBy(this, other,
        { it.start },
        { it.end }
    )

    fun toPair() = Pair(start, end)
}

class FreeRangeMap() {
    private var fsMapRange = sortedMapOf<Int, FreeRange>()
    private var fsFreeRangeSet = sortedSetOf<FreeRange>()

    fun addRange(range: Pair<Int, Int>) {
        val containsFirst = fsMapRange.containsKey(range.first)
        val containsSecond = fsMapRange.containsKey(range.second)
        val r: FreeRange

        //check if overlapping
        if (containsFirst && !containsSecond) {
            r = fsMapRange[range.first]!!
            r.end = range.second
        } else if (!containsFirst && containsSecond) {
            r = fsMapRange[range.second]!!
            r.start = range.first
        } else if (!containsFirst) {
            r = FreeRange(range.first, range.second)
            fsFreeRangeSet.add(r)
        } else {
            return
        }

        (range.first..range.second).forEach {
            fsMapRange[it] = r
        }

    }

    fun removeRange(range: Pair<Int, Int>) {
        val r = fsMapRange[range.first]!!
        if (r.end != range.second) {
            r.start = range.second + 1
        } else {
            fsFreeRangeSet.remove(r)
        }

        (range.first..range.second).forEach {
            fsMapRange.remove(it)
        }

    }

    fun getFreeRanges(): TreeSet<FreeRange> {
        return fsFreeRangeSet
    }

}

class DataMap() {
    val mutL = mutableListOf<Int>()
    var _id = 0
    private var fs = FreeRangeMap()
    private var blocks = mutableListOf<Pair<Int, Int>>()

    fun addBlock(lengthOfFile: Int) {
        blocks.add(Pair(mutL.size, mutL.size + lengthOfFile - 1))
        for (i in 0 until lengthOfFile) {
            mutL.add(_id)
        }
        _id += 1
    }

    fun addFreeSpace(lengthOfFree: Int) {
        if (lengthOfFree == 0) {
            return
        }
        var pos = mutL.size
        for (i in 0 until lengthOfFree) {
            mutL.add(-1)
        }
        fs.addRange(Pair(pos, pos + lengthOfFree - 1))
    }

    fun moveRange(fsRange: Pair<Int, Int>, blockRange: Pair<Int, Int>) {
        check(getSizeOfRange(fsRange) == getSizeOfRange(blockRange))
        if (fsRange.first > blockRange.first) {
            return
        }
        fs.removeRange(fsRange)
        fs.addRange(blockRange)

        getRangeFromPair(fsRange).zip(getRangeFromPair(blockRange)).forEach { (f, b) ->
            mutL[f] = mutL[b]
            mutL[b] = -1
        }
    }

    fun defragSingle(): Boolean {
        val freeSpaces = fs.getFreeRanges()
        if (freeSpaces.isEmpty()) return false
        val freeSpace = freeSpaces.first()
        val blIndex: Int
        try {
            blIndex = findSingleBlockFrom(freeSpace.start)
        } catch (e: NoSuchElementException) {
            return false
        }

        moveRange(Pair(freeSpace.start, freeSpace.start), Pair(blIndex, blIndex))
        return true
    }

    fun defragBlock() {

        blocks.reversed().forEach {
            val size = getSizeOfRange(it)

            try {
                val fr =fs.getFreeRanges().first {
                        el -> getSizeOfRange(el.toPair()) >= size
                }
                val diff = getSizeOfRange(fr.toPair()) - size
                moveRange(Pair(fr.start, fr.end-diff), it)
            } catch (e: NoSuchElementException) {
                //functional catch
            }
        }
    }

    private fun findSingleBlockFrom(start: Int): Int {
        return ((mutL.size - 1) downTo start).first {
            mutL[it] != -1
        }
    }

    private fun getSizeOfRange(range: Pair<Int, Int>): Int {
        return range.second - range.first + 1
    }

    private fun getRangeFromPair(pair: Pair<Int, Int>): IntRange {
        return pair.first..pair.second
    }

    private fun getPartDataMap(start: Int, end: Int): String {
        val sb = StringBuilder()
        mutL.subList(start, end).forEach {
            if (it != -1) {
                sb.append("$it")
            } else {
                sb.append(".")
            }
        }
        return sb.toString()
    }

    fun printDataMap() {
        println(getPartDataMap(0, mutL.size))
    }

}




