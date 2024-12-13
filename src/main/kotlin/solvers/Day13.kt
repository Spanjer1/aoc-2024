package nl.reinspanjer.solvers

private operator fun Pair<Long, Long>.times(it: Long): Pair<Long,Long> {
    return Pair(first * it, second * it)
}

private operator fun Pair<Long, Long>.div(it: Pair<Long,Long>): Pair<Long,Long> {
    return Pair(first / it.first, second / it.second)
}

private operator fun Pair<Long, Long>.minus(it: Pair<Long,Long>): Pair<Long,Long> {
    return Pair(first - it.first, second - it.second)
}

class Day13 : Solver {

    override fun partOne(input: List<String>): Number {
        val clawMachines: List<ClawMachine> = getClawMachines(input, 0)

        return clawMachines.fold(0L){ acc, clawMachine ->
            val ans = calculateCheapestBruteForce(clawMachine)
            if (ans != null) {
                acc + (ans.first*3) + ans.second
            } else {
                acc
            }
        }

    }

    private fun getClawMachines(input: List<String>, addValue: Long): List<ClawMachine> {
        val clawMachines: List<ClawMachine> = input.filter { it.isNotEmpty() }.chunked(3).map {
            val buttonA = it[0].split("Button A: X")[1].split(", Y")
            val buttonB = it[1].split("Button B: X")[1].split(", Y")
            val prize = it[2].split("Prize: X=")[1].split(", Y=")

            val buttonAPair = Pair(buttonA[0].toLong(), buttonA[1].toLong())
            val buttonBPair = Pair(buttonB[0].toLong(), buttonB[1].toLong())
            val prizePair = Pair(prize[0].toLong() + addValue, prize[1].toLong() + addValue)
            ClawMachine(buttonAPair, buttonBPair, prizePair)
        }
        return clawMachines
    }

    private fun calculateCheapestBruteForce(cm: ClawMachine): Pair<Long,Long>? {
        val buttonA = (0L..100L).map{ cm.buttonA * it }
        val buttonB = (100L downTo 0L).map{ cm.buttonB * it }.filter{ it.first <= cm.prize.first
                                                                    && it.second <= cm.prize.second }
        buttonB.forEach { b ->
             buttonA.forEach{ a ->
                 if ((b.first + a.first) == cm.prize.first &&  (b.second + a.second) == cm.prize.second) {
                     return Pair( (a/cm.buttonA).first, (b/cm.buttonB).first)
             } }
        }

        return null
    }

    private fun calculateCheapestSmart(cm: ClawMachine): Pair<Long,Long>? {
        val one = Pair(
            Pair(cm.buttonA.first, cm.buttonB.first),
            cm.prize.first
        )
        val two = Pair(
            Pair(cm.buttonA.second, cm.buttonB.second),
            cm.prize.second
        )

        return calculateSimplified(one, two)
    }

    private fun switch(p: Pair<Pair<Long,Long>, Long>): Pair<Pair<Long,Long>, Long> {
        return Pair(Pair(p.first.second, p.first.first), p.second)
    }

    private fun calculateSimplified(one: Pair<Pair<Long,Long>, Long>,two: Pair<Pair<Long,Long>, Long>): Pair<Long, Long>? {

        val answerA = getButtonAmount(switch(two), switch(one))
        val answerB = getButtonAmount(two, one)

        if (answerA - answerA.toLong() != 0.0 || answerB - answerB.toLong() != 0.0) {
            return null
        }

        return Pair(answerA.toLong(), answerB.toLong())
    }

    private fun getButtonAmount(
        one: Pair<Pair<Long, Long>, Long>,
        two: Pair<Pair<Long, Long>, Long>
    ): Double {
        val eqOne = Pair(one.first.second * two.first.first, one.second * two.first.first)
        val eqTwo = Pair(two.first.second * one.first.first, two.second * one.first.first)
        val eqMerged = eqOne - eqTwo
        return eqMerged.second / eqMerged.first.toDouble()
    }


    override fun partTwo(input: List<String>): Number {
        val clawMachines: List<ClawMachine> = getClawMachines(input, 10000000000000L)
        return clawMachines.fold(0L){ acc, clawMachine ->
            val ans = calculateCheapestSmart(clawMachine)
            if (ans != null) {
                acc + (ans.first*3) + ans.second
            } else {
                acc
            }
        }
    }

}

data class ClawMachine(val buttonA: Pair<Long,Long>, val buttonB: Pair<Long,Long>, val prize: Pair<Long, Long>)