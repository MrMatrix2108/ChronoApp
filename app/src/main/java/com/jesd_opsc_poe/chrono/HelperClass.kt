package com.jesd_opsc_poe.chrono

class HelperClass {
    companion object {
        fun notAllSpaces(string: String): Boolean {
            val regex = Regex(".*\\S.*")
            val cob = regex.matches(string)
            return cob
        }
    }
}
