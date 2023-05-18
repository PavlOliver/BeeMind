package com.example.beemind.databaseData

import android.content.Context
import com.example.beemind.popUps.ErrorNotification
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
/**
 * This class contains basics operations with row of inspection data
 *
 */
class InspectionData {
    companion object {
        /**
        *Inserts a new cure data into the database.
        *@param title The title of the entry.
        *@param date The date of the entry in the format "dd.MM.yyyy HH:mm".
        *@param temperature The temperature recorded for the entry.
        *@param job The job associated with the entry.
        *@param weight The weight recorded for the entry.
        *@param hiveTemp The hive temperature recorded for the entry.
         *@param queen represents a presence of queen in Hive
        *@param context The context of the application.
        *@return true if insert was successful, false otherwise
         */
        fun insert(
            title: String,
            date: String,
            temperature: String,
            job: String,
            weight: String,
            hiveTemp: String,
            queen: String,
            context: Context
        ): Boolean {
            //date
            val inputDateFormat = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            val formattedDate: String
            try {
                val datum: Date = if (date.isNullOrEmpty())
                    Date()
                else
                    inputDateFormat.parse(date)!!
                formattedDate = outputDateFormat.format(datum)
            } catch (e: ParseException) {
                //ErrorNotification("Wrong Date",context)
                return false
            }
            //temperature
            val temperatureDouble: Double? = if (temperature.isNullOrEmpty())
                null
            else {
                try {
                    temperature.toDouble()
                } catch (e: java.lang.NumberFormatException) {
                    ErrorNotification("Temperature must contains numbers only!", context)
                    return false
                }
            }

            val weightDouble: Double? = if (weight.isNullOrEmpty())
                null
            else {
                try {
                    weight.toDouble()
                } catch (e: java.lang.NumberFormatException) {
                    ErrorNotification("Weight must contains numbers only!", context)
                    return false
                }
            }

            val hiveTempDouble: Double? = if (hiveTemp.isNullOrEmpty())
                null
            else {
                try {
                    hiveTemp.toDouble()
                } catch (e: java.lang.NumberFormatException) {
                    ErrorNotification("Hive Temperature must contains numbers only!", context)
                    return false
                }
            }

            Database(context).writableDatabase.execSQL("INSERT INTO INSPECTION (title,date,temperature,job,weight,hiveTemp,queen) values('$title','$formattedDate','$temperatureDouble','$job','$weightDouble','$hiveTempDouble','$queen')")
            return true
        }

        /**
         * @param context
         * @return days since last cure
         */
        fun getDurationSinceLast(context: Context): Int {
            val cursor = Database(context).readableDatabase.rawQuery(
                "SELECT date from INSPECTION ORDER BY date DESC",
                null
            )
            if (!cursor.moveToFirst()) {
                return -1
            }
            val index = cursor.getColumnIndex("date")
            if (index >= 0) {
                val dateS = cursor.getString(index)

                val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = inputDateFormat.parse(dateS)?.toInstant()?.atZone(ZoneId.systemDefault())
                    ?.toLocalDate()

                val dateNow = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

                return ChronoUnit.DAYS.between(date, dateNow).toInt()
            }
            return 0
        }

    }
}