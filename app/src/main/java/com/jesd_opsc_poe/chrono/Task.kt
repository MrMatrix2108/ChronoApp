package com.jesd_opsc_poe.chrono

import android.widget.ImageView
import java.util.*

data class Task(val name: String,
                val client: String,
                val categoryName: String,
                val description: String,
                val date : Date,
                val startTime: String,
                val endTime: String,
                val image: ImageView
)
