package nl.reinspanjer.solvers

import kotlin.math.abs

class Day02 : Solver {

    override fun partOne(input: List<String>): Int {

        val reports: List<List<Int>> = input.map { report -> report.split(" ").map { s -> s.toInt() } }
        val total = reports.fold(0) { acc, report ->
            val reportResult = getReportResult(report)
            acc + if (reportResult) 1 else 0
        }
        return total;
    }

    private fun getReportResult(report: List<Int>) =
        ((checkAllIncreasing(report) || checkAllDecreasing(report)) && checkRange(report))

    private fun getReportResultRecursive(report: List<Int>, n: Int): Boolean {
        if (n == report.size) {
            return false
        }
        val newReport = report.take(n).plus(report.drop(n + 1))
        if (getReportResult(newReport)) {
            return true
        }

        return getReportResultRecursive(report, n + 1)
    }

    private fun checkAllIncreasing(report: List<Int>): Boolean {
        return report.zipWithNext().all { (a, b) -> b > a }
    }

    private fun checkAllDecreasing(report: List<Int>): Boolean {
        return report.zipWithNext().all { (a, b) -> a > b }
    }

    private fun checkRange(report: List<Int>): Boolean {
        return report.zipWithNext().all { (a, b) ->
            val absDiff = abs(a - b)
            absDiff in 1..3
        }
    }

    override fun partTwo(input: List<String>): Int {
        val reports: List<List<Int>> = input.map { report -> report.split(" ").map { s -> s.toInt() } }
        val total = reports.fold(0) { acc, report ->
            val reportResult = getReportResult(report) || getReportResultRecursive(report, 0)
            acc + if (reportResult) 1 else 0
        }
        return total;
    }
}