package nl.reinspanjer
import nl.reinspanjer.common.getResource
import nl.reinspanjer.common.getSolver

fun main(args: Array<String>) {
    if (args.size != 3) {
        println("Please provide exactly 3 arguments: ")
        println("Day: The day you want to run")
        println("Part: The part you want to run")
        println("InputFile: The input file located in resources/dayx/<input-file>")
        println("Usage: program <day> <part> <input-file>")
        return
    }

    try {
        val day = args[0].toInt()
        val part = args[1].toInt()
        val text = args[2]
        val resource = getResource("day${day}/${text}")
        val solver = getSolver(day)

        val answer = if (part == 1) {
            solver.partOne(resource)
        } else {
            solver.partTwo(resource)
        }
        println("Answer part ${part}: $answer")

    } catch (e: NumberFormatException) {
        e.printStackTrace()
        println("Error: First two arguments must be valid numbers")
        println("Usage: program <number1> <number2> <string>")
    }
}