package com.jesd_opsc_poe.chrono

data class Category(val name: String,
                    val duration: String,
                    val lsTasks: List<Task>)