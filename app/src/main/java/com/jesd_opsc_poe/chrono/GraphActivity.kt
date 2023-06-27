package com.jesd_opsc_poe.chrono

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class GraphActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        auth = Firebase.auth

        val tvTasks = findViewById<TextView>(R.id.tvNotTaks)
        val totalHrsEntries : MutableList<Entry> = mutableListOf()

        var currentUser = auth.currentUser
        var userKey: String? = ""
        if (currentUser != null) {
             userKey = currentUser.email
        } else {
            // No user is signed in
        }
        val dbTasksref = FirebaseDatabase.getInstance().getReference("Tasks")
        var allTasks : HashMap<String, Task>?
        dbTasksref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                totalHrsEntries.clear()
                allTasks = snapshot.getValue<HashMap<String, Task>>()
                val userMap : Map<String, Task>? = allTasks?.filterValues { it.userKey == userKey}
                val todayMap : Map<String, Task>? = userMap?.filterValues { it.date == "2023-06-06" }
                tvTasks.text = totalHrsEntries.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("fail", "onCancelled : ${error.toException()}")
            }
        })



        val lineChart: LineChart = findViewById(R.id.lineChart)

        //this is the list of entries for the total hours line graph

//            Entry(1f, 7f),
//            Entry(2f, 12f),
//            Entry(3f, 10f),
//            Entry(4f, 15f),
//            Entry(5f, 8f),
//            Entry(6f, 15f),
//            Entry(7f, 8f),
//            Entry(8f, 10f),
//            Entry(9f, 15f),
//            Entry(10f, 8f)
//        )

        //this is the list of entries for the minimum hours goal line graph
        val minGoalEntries = listOf(
            Entry(1f, 2f),
            Entry(2f, 5f),
            Entry(3f, 8f),
            Entry(4f, 4f),
            Entry(5f, 3f),
            Entry(6f, 1f),
            Entry(7f, 8f),
            Entry(8f, 10f),
            Entry(9f, 15f),
            Entry(10f, 8f)
        )

        //this is the list of entries for the maximum hours goal line graph
        val maxGoalEntries = listOf(
            Entry(1f, 3f),
            Entry(2f, 6f),
            Entry(3f, 4f),
            Entry(4f, 9f),
            Entry(5f, 12f),
            Entry(6f, 3f),
            Entry(7f, 5f),
            Entry(8f, 11f),
            Entry(9f, 14f),
            Entry(10f, 9f)
        )

        val totalHrsDataset = LineDataSet(totalHrsEntries, "Total")
        totalHrsDataset.color = Color.RED // Set the line color
        totalHrsDataset.setDrawCircles(true)
        totalHrsDataset.setDrawValues(false)

        val minGoalDataset = LineDataSet(minGoalEntries, "Min Goal")
        minGoalDataset.color = Color.BLUE // Set the line color
        minGoalDataset.setDrawCircles(true)
        minGoalDataset.setDrawValues(false)

        val maxGoalDataset = LineDataSet(maxGoalEntries, "Max Goal")
        maxGoalDataset.color = Color.BLACK // Set the line color
        maxGoalDataset.setDrawCircles(true)
        maxGoalDataset.setDrawValues(false)

        val graphData = LineData(totalHrsDataset,minGoalDataset,maxGoalDataset)
        lineChart.data = graphData

        val xAxisLine: XAxis = lineChart.xAxis
        xAxisLine.position = XAxis.XAxisPosition.BOTTOM // Set X Axis position
        xAxisLine.setDrawGridLines(false)
        val yAxisLine: YAxis = lineChart.axisLeft
        yAxisLine.setDrawGridLines(false)
        lineChart.axisRight.isEnabled = false

        val description: Description = lineChart.description
        description.text = ""

        lineChart.invalidate()
    }




}