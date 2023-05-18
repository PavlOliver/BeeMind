package com.example.beemind.databaseData

import android.content.Context
import com.example.beemind.popUps.ErrorNotification
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

/**
 * This class contains basics operations with row of cure data
 *
 */
class CureData {

    companion object {
        /**
        Inserts a new cure data into the database.
        @param title The title of the entry.
        @param date The date of the entry in the format "dd.MM.yyyy HH:mm".
        @param temperature The temperature recorded for the entry.
        @param job The job associated with the entry.
        @param weight The weight recorded for the entry.
        @param hiveTemp The hive temperature recorded for the entry.
        @param context The context of the application.
         @return true if insert was successful, false otherwise
         */
        fun insert(
            title: String,
            date: String,
            temperature: String,
            job: String,
            weight: String,
            hiveTemp: String,
            context: Context
        ): Boolean {
            //date
            val inputDateFormat = SimpleDateFormat("dd.MM.yy HH:mm", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val formattedDate: String
            try {
                val datum: Date = if (date.isEmpty())
                    Date()
                else
                    inputDateFormat.parse(date)!!
                formattedDate = outputDateFormat.format(datum)
            } catch (e: ParseException) {
                return false
            }
            //temperature
            val temperatureDouble: Double? = if (temperature.isEmpty())
                null
            else {
                try {
                    temperature.toDouble()
                } catch (e: java.lang.NumberFormatException) {
                    ErrorNotification("Temperature must contains numbers only!", context)
                    return false
                }
            }
            //weight
            val weightDouble: Double? = if (weight.isEmpty())
                null
            else {
                try {
                    weight.toDouble()
                } catch (e: java.lang.NumberFormatException) {
                    ErrorNotification("Weight must contains numbers only!", context)
                    return false
                }
            }
            //hiveTemp
            val hiveTempDouble: Double? = if (hiveTemp.isEmpty())
                null
            else {
                try {
                    hiveTemp.toDouble()
                } catch (e: java.lang.NumberFormatException) {
                    ErrorNotification("Hive Temp must contains numbers only!", context)
                    return false
                }
            }

            Database(context).writableDatabase.execSQL("INSERT INTO CURE (title,date,temperature,job,weight,hiveTemp) values('$title','$formattedDate','$temperatureDouble','$job','$weightDouble','$hiveTempDouble')")
            return true
        }

        /**
         * @param context
         * @return days since last cure
         */
        fun getDurationSinceLast(context: Context): Int {
            val cursor = Database(context).readableDatabase.rawQuery(
                "SELECT date from CURE ORDER BY date DESC",
                null
            )
            if (!cursor.moveToFirst()) {
                cursor.close()
                return -1
            }
            val index = cursor.getColumnIndex("date")
            if (index >= 0) {
                val dateS = cursor.getString(index)

                val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val date = inputDateFormat.parse(dateS)?.toInstant()?.atZone(ZoneId.systemDefault())
                    ?.toLocalDate()

                val dateNow = Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                cursor.close()
                return ChronoUnit.DAYS.between(date, dateNow).toInt()
            }
            cursor.close()
            return 0
        }

    }
}