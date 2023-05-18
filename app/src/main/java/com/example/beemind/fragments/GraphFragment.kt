package com.example.beemind.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.beemind.databaseData.Database
import com.example.beemind.databinding.FragmentGraphBinding
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.LegendRenderer
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Class manage a Graphs and data which graph display
 *
 */
class GraphFragment : Fragment() {
    private lateinit var database: Database
    private lateinit var graph: GraphView

    /**
     * is used to initialize database
     * @param context of the application
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        database = Database(context)
    }

    /**
     * creates appearance of GraphFragment
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val hiveTitle = arguments?.getString("1")
        val binding = FragmentGraphBinding.inflate(inflater, container, false)


        this.graph = binding.graph
        if (hiveTitle != null) {
            this.load(hiveTitle, false)
            binding.switchBtn.setOnCheckedChangeListener { _, isChecked ->
                this.graph.removeAllSeries()
                if (isChecked) {
                    binding.titleOfFrag.text = "Today's Graph"
                } else {
                    binding.titleOfFrag.text = "Month's Graph"
                }
                this.load(hiveTitle, isChecked)
            }

        }


        return binding.root
    }

    /**
     * loads data from database and display's it to graphView
     *
     * @param hiveTitle name of the title which data we're seeing
     * @param today true if we're seeing today's data, false if data from last month
     * @return
     */
    private fun load(hiveTitle: String, today: Boolean): View {
        val series = LineGraphSeries(
            arrayOf(
            )
        )
        val series2 = LineGraphSeries(
            arrayOf(
            )
        )
        val series3 = LineGraphSeries(
            arrayOf()
        )
        series.title = "Temperature"
        series2.title = "Humidity"
        series3.title = "Weight"
        series2.color = Color.RED
        series3.color = Color.GREEN
        val cursor: Cursor = if (today) {
            database.readableDatabase.rawQuery(
                "SELECT date,temperature,humidity,weight FROM MONITORING WHERE title='$hiveTitle'  AND strftime('%Y-%m-%d', date) =  strftime('%Y-%m-%d', datetime('now'))",
                null
            )
        } else {
            database.readableDatabase.rawQuery(
                "SELECT date,temperature,humidity,weight FROM MONITORING WHERE title='$hiveTitle' AND strftime('%Y-%m', date) =  strftime('%Y-%m', datetime('now'))",
                null
            )
        }

        while (cursor.moveToNext()) {
            val dateIndex = cursor.getColumnIndex("date")
            val dateString = cursor.getString(dateIndex)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(dateString)
            val hoursFormat: SimpleDateFormat = if (today)
                SimpleDateFormat("HH", Locale.getDefault())
            else
                SimpleDateFormat("dd", Locale.getDefault())
            val totalHours = hoursFormat.format(date!!).toDouble()

//
            val hiveTempIndex = cursor.getColumnIndex("temperature")
            val hiveTempString = cursor.getString(hiveTempIndex)
            val hiveTemp: Double? = if (hiveTempString.isNullOrEmpty()) {
                null
            } else {
                hiveTempString.toDouble()
            }

            val humidityIndex = cursor.getColumnIndex("humidity")
            val humidutyString = cursor.getString(humidityIndex)
            val humidity: Double? = if (humidutyString.isNullOrEmpty()) {
                null
            } else {
                humidutyString.toDouble()
            }

            val weightIndex = cursor.getColumnIndex("weight")
            val weightString = cursor.getString(weightIndex)
            val weight: Double? = if (weightString.isNullOrEmpty()) {
                null
            } else {
                weightString.toDouble()
            }
            Log.d(TAG, "total $totalHours and weight is $weight")
            if (hiveTemp != null) {
                series.appendData(DataPoint(totalHours, hiveTemp.toDouble()), true, 10)
            }
            if (humidity != null)
                series2.appendData(DataPoint(totalHours, humidity.toDouble()), true, 10)
            if (weight != null)
                series3.appendData(DataPoint(totalHours, weight.toDouble()), true, 10)
        }
        cursor.close()

        val gridLabelRenderer = graph.gridLabelRenderer
        gridLabelRenderer.horizontalAxisTitle = "Time"
        graph.legendRenderer.isVisible = true
        graph.legendRenderer.align = LegendRenderer.LegendAlign.TOP
        graph.legendRenderer.backgroundColor = Color.TRANSPARENT
        gridLabelRenderer.numHorizontalLabels = 5
        gridLabelRenderer.numVerticalLabels = 15

        graph.viewport.isXAxisBoundsManual = true
        //graph.viewport.isYAxisBoundsManual = true
        //graph.viewport.setMinY(0.0)
        //graph.viewport.setMaxY(101.0)
        if (today) {
            graph.viewport.setMinX(0.0)
            graph.viewport.setMaxX(23.0)
        } else {
            graph.viewport.setMinX(0.0)
            graph.viewport.setMaxX(31.0)
        }
        //graph.viewport.isScalable = true

        graph.addSeries(series)
        graph.addSeries(series2)
        graph.addSeries(series3)

        return this.graph
    }
}