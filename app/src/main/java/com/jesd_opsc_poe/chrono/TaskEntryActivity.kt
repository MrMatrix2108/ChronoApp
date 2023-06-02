package com.jesd_opsc_poe.chrono

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class TaskEntryActivity : AppCompatActivity() {

    private lateinit var btnGotoInsights: AppCompatButton
    private lateinit var btnGotoTaskEntry: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_entry)
    }
}