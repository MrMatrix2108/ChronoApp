package com.jesd_opsc_poe.chrono

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
import com.jakewharton.threetenabp.AndroidThreeTen
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.LocalDate
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class GraphActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        auth = Firebase.auth
        AndroidThreeTen.init(this)
        val lineChart: LineChart = findViewById(R.id.lineChart)
        val lsTimePeriods = listOf<String>("Last 10 days", "Last 20 days", "Last 30 days")


        val spinner = findViewById<Spinner>(R.id.spinTimePeriod)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, lsTimePeriods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter



        var selectedItem: String? = null
        var noOfDays: Int = 10

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedItem = p0?.selectedItem.toString()
                updateLineChart(selectedItem!!,noOfDays)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) { // Handle the case when nothing is selected
                selectedItem = null
            }
        }



    }

    private fun convertTimeToFloat(time: String): Float {
        val parts = time.split(":")
        val hours = parts.get(0).toInt()
        val minutes = parts.get(1).toInt()
        val floatTime = hours.plus(((minutes.toFloat()) / 60))
        return floatTime
    }

    private fun calculateDailyTotalHours(tasks: Map<String, Task>?, desiredDate: String?): DailyTotal {
        var totalHours = 0f
        if (tasks != null) {
            for (task in tasks.values) {
                if (task.date == desiredDate) {
                    totalHours += convertTimeToFloat(task.duration!!)
                }
            }
        }

        return DailyTotal(totalHours, desiredDate)
    }

    private fun getDatesInRange(startDate: String, lastDate: String): List<String> {
        val dates = mutableListOf<String>()
        var currentDate = LocalDate.parse(startDate)
        var endDate = LocalDate.parse(lastDate)

        while (!currentDate.isAfter(endDate)) {
            dates.add(currentDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
            currentDate = currentDate.plusDays(1)
        }

        return dates
    }

    private fun minusDaysFromCurrentDate(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val resultDate: Date = calendar.time
        return dateFormat.format(resultDate)
    }

    private fun updateLineChart(selectedItem: String, num: Int) {

        val totalHrsEntries : MutableList<Entry> = mutableListOf()
        val lineChart: LineChart = findViewById(R.id.lineChart)
        var noOfDays = num

        when(selectedItem){
            "Last 10 days" -> {
                noOfDays = 10
            }
            "Last 20 days" -> {
                noOfDays = 20
            }
            "Last 30 days" -> {
                noOfDays = 30
            }
        }

        var currentUser = auth.currentUser
        var userKey: String? = ""
        if (currentUser != null) {
            userKey = currentUser.email
        } else {
            // No user is signed in
        }
        val dbTasksref = FirebaseDatabase.getInstance().getReference("Tasks")
        var allTasks : HashMap<String, Task>?

        dbTasksref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalHrsEntries.clear()
                allTasks = snapshot.getValue<HashMap<String, Task>>()
                val userMap: Map<String, Task>? =
                    allTasks?.filterValues { it.userKey == userKey }

                val calendar = Calendar.getInstance()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayDate: Date = calendar.time
                var startDate : String = minusDaysFromCurrentDate(noOfDays)
                var endDate : String=  dateFormat.format(todayDate)
                var dates : List<String> = getDatesInRange(startDate, endDate)

                var dailyTotals : MutableList<DailyTotal> = mutableListOf<DailyTotal>()
                for(date in dates){
                    dailyTotals.add(calculateDailyTotalHours(userMap, date))
                }
                if (dailyTotals != null) {
                    var i: Float = 1F
                    for (dailyTotal in dailyTotals) {

                        totalHrsEntries.add(Entry(i, dailyTotal.time))
                        i++
                    }




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

                    val graphData = LineData(totalHrsDataset)
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

            override fun onCancelled(error: DatabaseError) {
                Log.e("fail", "onCancelled : ${error.toException()}")
            }
        })
    }
}