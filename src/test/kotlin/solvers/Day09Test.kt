//package solvers
//
//import nl.reinspanjer.solvers.DataMap
//import org.junit.jupiter.api.Assertions.assertEquals
//import org.junit.jupiter.api.Test
//
//class Day09Test {
//
//    private fun getTestDataMap(input: String): DataMap {
//
//        val chunked = input.chunkedSequence(2)
//        val dm = DataMap()
//        chunked.forEach {
//            dm.addBlock(it[0].toString().toInt())
//            if (it.length == 2) {
//                dm.addFreeSpace(it[1].toString().toInt())
//            }
//        }
//        return dm
//    }
//
//    @Test
//    fun testSum() {
//        val input = "2333133121414131402"
//        val dm = getTestDataMap(input)
//        val output = dm.getPartDataMap(0, dm.mutL.size)
//        assertEquals(output, "00...111...2...333.44.5555.6666.777.888899")
//        assertEquals(dm.mutL.size, input.fold(0) { acc, n -> acc + n.toString().toInt() })
//    }
//
//    @Test
//    fun testSum2() {
//        val dm = getTestDataMap("233")
//        dm.moveRange(Pair(2,4), Pair(5,7))
//        assertEquals(dm.getPartDataMap(0, dm.mutL.size), "00111...")
//    }
//
//    @Test
//    fun testSum3() {
//        val dm = getTestDataMap("233")
//        dm.moveRange(Pair(2,2), Pair(5,5))
//        dm.moveRange(Pair(3,3), Pair(6,6))
//        dm.moveRange(Pair(4,4), Pair(7,7))
//        assertEquals(dm.getPartDataMap(0, dm.mutL.size), "00111...")
//    }
//
//
//}