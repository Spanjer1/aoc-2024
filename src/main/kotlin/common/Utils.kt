package nl.reinspanjer.common

import nl.reinspanjer.solvers.Solver

fun getResource(path: String): List<String> {
    println("Loading $path")
    return object {}.javaClass.classLoader.getResourceAsStream(path)!!.reader().readLines()
}

fun getSolver(number: Int): Solver {
    val numberText = if (number <= 9) {
        "0$number"
    } else {
        "$number"
    }
    val className = "nl.reinspanjer.solvers.Day${numberText}"
    println("Loading $className")
    val clazz = Class.forName(className)

    return clazz.getDeclaredConstructor().newInstance() as Solver
}