package com.jesd_opsc_poe.chrono

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class InsightsActivity : AppCompatActivity() {

    private lateinit var btnGotoYourActivity: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insights)

        btnGotoYourActivity = findViewById(R.id.btnGotoYourActivity)

        btnGotoYourActivity.setOnClickListener {
            val intent = Intent(this, TimesheetActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
