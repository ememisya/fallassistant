package com.ememisya.fallassistant.util

import kotlin.math.min

/**
 * Utility for string matching using Levenshtein distance.
 */
object MatchUtil {

    /**
     * Calculates the Levenshtein distance between two strings.
     *
     * @param a The first string.
     * @param b The second string.
     * @return The integer distance representing character modifications needed.
     */
    fun levenshtein(a: String, b: String): Int {
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j

        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = min(
                    min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                )
            }
        }
        return dp[a.length][b.length]
    }

    /**
     * Determines if a target string matches an input within a certain error margin.
     *
     * @param input The raw input string.
     * @param targets A list of valid target strings.
     * @param maxDistance The maximum allowable modifications.
     * @return True if a close match is found.
     */
    fun isCloseMatch(input: String, targets: List<String>, maxDistance: Int = 3): Boolean {
        val cleaned = input.lowercase().trim()
        return targets.any { levenshtein(cleaned, it.lowercase()) <= maxDistance }
    }
}