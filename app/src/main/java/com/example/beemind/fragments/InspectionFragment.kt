package com.example.beemind.fragments

import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.ActivityInfo
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.beemind.databaseData.Database
import com.example.beemind.databaseData.InspectionData
import com.example.beemind.popUps.ErrorNotification
import com.example.beemind.R
import java.text.SimpleDateFormat
import java.util.*
import com.example.beemind.databinding.FragmentInspectionBinding
import com.example.beemind.databinding.InspectionHistoryLineBinding
import java.text.ParseException

/**
 * Class is managing a functionality of InspectionFragment
 */
class InspectionFragment : Fragment() {
    private lateinit var database: Database
    private lateinit var temp: String
    private var job: String? = null
    private var weight: String? = null
    private var hiveTemp: String? = null
    private lateinit var datum: String
    private var title: String? = null
    private lateinit var queen:String
    private lateinit var mainLay: LinearLayout
    private lateinit var viewReload: View

    /**
     * is used to initialize database
     * @param context of the application
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        database = Database(context)
    }

    /**
     * loads a single row from a database
     */
    private fun load() {
        val cursor = database.readableDatabase.rawQuery(
            "SELECT * FROM INSPECTION WHERE title=? ",
            arrayOf(this.title), null
        )
        while (cursor.moveToNext()) {
            val dateIndex = cursor.getColumnIndex("date")
            val temperatureIndex = cursor.getColumnIndex("temperature")
            val jobIndex = cursor.getColumnIndex("job")
            val weightIndex = cursor.getColumnIndex("weight")
            val hiveTempIndex = cursor.getColumnIndex("hiveTemp")
            val queenIndex = cursor.getColumnIndex("queen")

            this.datum = cursor.getString(dateIndex)
            this.temp = cursor.getString(temperatureIndex)
            this.job = cursor.getString(jobIndex)
            this.weight = cursor.getString(weightIndex)
            this.hiveTemp = cursor.getString(hiveTempIndex)
            this.queen = cursor.getString(queenIndex)
            this.mainLay.addView(this.insert())
        }
        cursor.close()
    }

    /**
     * inserts loaded rows of inspection history data to tableView of fragment
     *
     * @return view which contains loaded history lines
     */
    private fun insert(): View {
        val binding = InspectionHistoryLineBinding.inflate(layoutInflater, this.mainLay, false)
        val inputFormat = SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(datum)
        val dateStr = outputDateFormat.format(date!!)
        binding.dateT.text = dateStr
        binding.tempT.text = if(temp == "null") "-" else temp
        binding.jobT.text = job
        binding.weightT.text = if(weight.equals("null")) {
            "-"
        } else {
            weight
        }
        binding.hiveTempT.text = if(hiveTemp.equals("null")) "-"
        else{
            hiveTemp
        }
        binding.queenT.text = this.queen
        return binding.root
    }

    /**
     * @return view with updated line after add button pressed
     */
    private fun inserter(): View {
        this.viewReload = layoutInflater.inflate(R.layout.inspection_line, this.mainLay, false)
        val btn = this.viewReload.findViewById<ImageButton>(R.id.plusBtn)
        btn.setOnClickListener {
            val date = this.viewReload.findViewById<EditText>(R.id.date).text.toString()
            if (date.isEmpty()) {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                this.datum = inputFormat.format(Date())
            } else {
                try {
                    val outputDateFormat =
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val inputFormat = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
                    val dDate = inputFormat.parse(date)
                    this.datum = outputDateFormat.format(dDate)
                } catch(e: ParseException){
                    ErrorNotification("Date is in wrong format",requireContext())
                }
            }
            val jobObj = this.viewReload.findViewById<EditText>(R.id.job).text.toString()
            if(jobObj.isNotEmpty()) {
                this.job = jobObj
                val tempObj = this.viewReload.findViewById<EditText>(R.id.temp).text.toString()
                this.temp = tempObj
                val weightObj = viewReload.findViewById<EditText>(R.id.weight).text.toString()
                this.weight = weightObj
                val hiveTempObj =
                    this.viewReload.findViewById<EditText>(R.id.hiveTemp).text.toString()
                this.hiveTemp = hiveTempObj
                val queenObj = this.viewReload.findViewById<EditText>(R.id.queen).text.toString()
                this.queen = queenObj
                try {
                    if (!InspectionData.insert(
                            this.title!!,
                            date,
                            tempObj,
                            jobObj,
                            weightObj,
                            hiveTempObj,
                            queenObj,
                            requireContext()
                        )
                    )
                    else {
                        mainLay.removeView(this.viewReload)
                        mainLay.addView(this.insert())
                        mainLay.addView(this.inserter())
                    }

                } catch (error: SQLiteConstraintException) {
                    error.printStackTrace()
                    context?.let { it1 -> ErrorNotification("Unique constraint failed", it1) }
                }
            }
            else{
                ErrorNotification("Job Description can not be Empty!",requireContext())
            }
        }
        return this.viewReload
    }

    /**
     * Creates appearance of fragment and functionality of button
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
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        val binding = FragmentInspectionBinding.inflate(inflater, container, false)
        this.mainLay = binding.mainLay
        this.title = arguments?.getString("1")
        Log.d(TAG, "Som v $title")

        this.load()
        mainLay.addView(this.inserter())

        return binding.root
    }


}