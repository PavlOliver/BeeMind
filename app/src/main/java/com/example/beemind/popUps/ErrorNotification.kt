package com.example.beemind.popUps

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import com.example.beemind.R

/**
 * ErrorNotification class is responsible for displaying error messages in an AlertDialog.
 * @constructor Creates an instance of ErrorNotification with a message and a context.
 */
class ErrorNotification {
    constructor(message:String,context:Context){
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
        builder.setTitle("Error")
        builder.setIcon(R.drawable.baseline_error_24)
        builder.setCancelable(true)
        builder.create().show()
    }
    constructor(message:String,activity:Activity){
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(message)
        builder.setTitle("Error")
        builder.setIcon(R.drawable.baseline_error_24)
        builder.setCancelable(true)
        builder.create().show()
    }
}