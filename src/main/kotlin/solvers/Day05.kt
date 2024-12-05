package nl.reinspanjer.solvers

class Day05 : Solver {

    override fun partOne(input: List<String>): Int {
        val (orderMap, updateList) = getParsedInput(input)
        var total = 0
        updateList.forEach { updateLine ->
            if (checkIfRuleCorrect(updateLine, orderMap)) {
                val middle = updateLine.size/2
                total += updateLine[middle]
            }
        }

        return total;
    }

    private fun getParsedInput(input: List<String>): Pair<MutableMap<Int, Rule>, MutableList<List<Int>>> {
        val orderMap = mutableMapOf<Int, Rule>()
        val updateList = mutableListOf<List<Int>>()
        var part = 0
        input.forEach { line ->
            if (line == "") {
                part = 1
                return@forEach
            }
            if (part == 0) {
                val split = line.split('|')
                val left = split[0].toInt()
                val right = split[1].toInt()

                val ruleL = orderMap.getOrDefault(left, Rule())
                val ruleR = orderMap.getOrDefault(right, Rule())
                ruleL.after.add(right)
                ruleR.before.add(left)
                orderMap[left] = ruleL
                orderMap[right] = ruleR
            } else {
                val split = line.split(',').map { it.toInt() }
                updateList.add(split)
            }
        }
        return Pair(orderMap, updateList)
    }

    private fun checkIfRuleCorrect(updateLine: List<Int>, rules: Map<Int, Rule>): Boolean {
        var ret = true
        run breaking@ {
            updateLine.forEachIndexed { index, n ->
                val rule = rules.getOrDefault(n, Rule())
                val left = updateLine.subList(0, index)
                val right = updateLine.subList(index + 1, updateLine.size)
                val before = rule.before
                val after = rule.after
                val leftCheck = left.all{ before.contains(it) || !after.contains(it)}
                val rightCheck = right.all {after.contains(it) ||  !before.contains(it)}
                if (!(leftCheck && rightCheck)) {
                    ret = false
                    return@breaking
                }
            }
        }
        return ret
    }

    private fun fixWrongList(updateLine: List<Int>, rules: Map<Int, Rule>): List<Int> {
        val fixedNumbers: MutableList<Int> = updateLine.toMutableList()

        while (!checkIfRuleCorrect(fixedNumbers, rules) ) {
            for ((index, n) in fixedNumbers.withIndex()) {
                val rule = rules.getOrDefault(n, Rule())
                val right = fixedNumbers.subList(index + 1, fixedNumbers.size)
                val after = rule.after
                val rightCheck = right.filter { !after.contains(it) }

                 if (rightCheck.isNotEmpty()) {
                    val violated = rightCheck[0]
                    fixedNumbers[fixedNumbers.indexOf(violated)] = n
                    fixedNumbers[index] = violated
                    break
                }
            }
        }


        return fixedNumbers
    }


    override fun partTwo(input: List<String>): Int {
        val (orderMap, updateList) = getParsedInput(input)
        var total = 0
        updateList.forEach { updateLine ->
            if (!checkIfRuleCorrect(updateLine, orderMap)) {
                val fixed = fixWrongList(updateLine, orderMap)
                total += fixed[fixed.size/2]
            }
        }
        return total;
    }
}

class Rule() {
    val before: MutableList<Int> = mutableListOf()
    val after: MutableList<Int> = mutableListOf()
}