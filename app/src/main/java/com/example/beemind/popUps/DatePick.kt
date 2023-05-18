package com.example.beemind.popUps

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.example.beemind.databaseData.Tasks
import com.example.beemind.databinding.PlannerPopupBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * Class is used to manage PopUp Windows which
 * is used to create a task
 */
class DatePick {

    private lateinit var popupWindow: PopupWindow

    /**
     * Method will create and show PopUpWindow and declare actions in it
     *
     * @param context of application
     */
    fun create(context: Context) {
        val popupBinding = PlannerPopupBinding.inflate(LayoutInflater.from(context))
        val datePicker = popupBinding.datePicker
        val timePicker = popupBinding.timePicker
        timePicker.setIs24HourView(true)
        val taskEdit = popupBinding.task
        val popupButton = popupBinding.popupButton
        this.popupWindow = PopupWindow(
            popupBinding.root,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        popupWindow.isFocusable = true
        popupWindow.isTouchable = true
        popupWindow.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
        popupButton.setOnClickListener {
            if (taskEdit.text.isNotEmpty()) {
                if (popupBinding.check.isChecked) {
                    Tasks.insert(taskEdit.text.toString(), context)
                } else {
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
                    val date =
                        "${datePicker.dayOfMonth}-${datePicker.month + 1}-${datePicker.year} ${timePicker.hour}:${timePicker.minute}"
                    val formattedDate = dateFormat.parse(date)
                    if (formattedDate != null) {
                        taskEdit.hint = formattedDate.toString()
                    }
                    val calendar = Calendar.getInstance()
                    if (formattedDate != null) {
                        calendar.time = formattedDate
                    }
                    val alarmTime = calendar.timeInMillis
                    val alarmManager =
                        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(context, LocalNotification::class.java)
                    intent.putExtra("1", "${taskEdit.text}")
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_MUTABLE
                    )
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
                }
            }
            this.popupWindow.dismiss()
        }
        popupWindow.showAtLocation(popupBinding.root, Gravity.CENTER, 0, 0)
    }

}