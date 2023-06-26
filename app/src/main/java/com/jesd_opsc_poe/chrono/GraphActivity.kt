package com.jesd_opsc_poe.chrono

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class GraphActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        val lineChart: LineChart = findViewById(R.id.lineChart)

        //this is the list of entries for the total hours line graph
        val totalHrsEntries = listOf(
            Entry(1f, 7f),
            Entry(2f, 12f),
            Entry(3f, 10f),
            Entry(4f, 15f),
            Entry(5f, 8f),
            Entry(6f, 15f),
            Entry(7f, 8f),
            Entry(8f, 10f),
            Entry(9f, 15f),
            Entry(10f, 8f)
        )

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