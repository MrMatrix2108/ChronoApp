package com.jesd_opsc_poe.chrono

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class TimesheetActivity : AppCompatActivity() {

    private lateinit var btnGotoInsights: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timesheet)

         btnGotoInsights = findViewById(R.id.btnGotoInsights)

        btnGotoInsights.setOnClickListener {
            val intent = Intent(this, InsightsActivity::class.java)
            startActivity(intent)

        }
    }
}