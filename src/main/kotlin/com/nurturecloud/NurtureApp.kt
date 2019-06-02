package com.nurturecloud

import com.nurturecloud.domain.Query
import com.nurturecloud.search.SuburbFinder
import java.util.*

object NurtureApp {

    private val QUIT = "Q"
    private val suburbFinder = SuburbFinder()

    @JvmStatic
    fun main(args: Array<String>) {
        val command = Scanner(System.`in`)
        var running = true
        while (running) {

            print("Welcome to Nurture Cloud Suburb Search. To Exit, enter q (quit) as a suburb name\n\n")

            print("Please enter a suburb name: ")
            val suburbName = command.nextLine()

            if (suburbName.equals(QUIT, ignoreCase = true))
                running = false
            else {
                if (invalid(suburbName, false)) {
                    print("\nERROR: Invalid Suburb Name. \n\n")
                    continue
                }

                print("Please enter the postcode: ")
                val postcode = command.nextLine()
                if (invalid(postcode, true)) {
                    print("\nERROR: Invalid PostCode Number. \n\n")
                    continue
                }

                val nearbySuburbs = suburbFinder.find(Query(suburbName, Integer.parseInt(postcode)), SuburbFinder.NEARBY)
                val fringeSuburbs = suburbFinder.find(Query(suburbName, Integer.parseInt(postcode)), SuburbFinder.FRINGE)

                if (nearbySuburbs.isEmpty() && fringeSuburbs.isEmpty()) {
                    println(String.format("Nothing found for %s, %s!!\n", suburbName, postcode))
                } else {
                    println("\n\nNearby Suburbs:\n")
                    nearbySuburbs.forEach { (postcode1, locality) -> println(String.format("\t\t\t%s %s\n", locality, postcode1)) }

                    println("\n\nFringe Suburbs:\n")
                    fringeSuburbs.forEach { (postcode1, locality) -> println(String.format("\t\t\t%s %s\n", locality, postcode1)) }
                }

            }
        }
        command.close()
    }

    private fun invalid(string: String?, isNumber: Boolean): Boolean {
        return string == null || string.isEmpty() || if (isNumber) isText(string) else isNumber(string)
    }

    private fun isNumber(string: String): Boolean {
        return string.matches("[0-9]+".toRegex())
    }

    private fun isText(string: String): Boolean {
        return string.matches("[a-zA-Z]+".toRegex())
    }
}
