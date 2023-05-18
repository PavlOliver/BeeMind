package com.example.beemind

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import com.example.beemind.databaseData.Database
import com.example.beemind.popUps.ErrorNotification
import kotlinx.coroutines.*
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Create bluetooth communication between mobile application and Hive
 * @property address MAC address off Bluetooth device inside of Hive
 */
class BluetoothCommunication(private var address: String) {
    private val uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    private lateinit var socket: BluetoothSocket

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var connected: Boolean = false
    private lateinit var title: String
    private lateinit var context: Context

    /**
     * Create bluetooth connection and socket
     *
     * @param activity of application
     * @param title of hive
     */
    fun start(activity: Activity, title: String) {
        this.context = activity.baseContext
        this.title = title
        val bluetoothManager =
            activity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        this.bluetoothAdapter = bluetoothManager.adapter
        if (!bluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(activity, enableBtIntent, 1, null)
        } else {
            if (ActivityCompat.checkSelfPermission(
                    activity.baseContext,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.BLUETOOTH_ADMIN),
                    1001
                )
            }
            try {
                val device = bluetoothAdapter.getRemoteDevice(address)
                socket = device.createInsecureRfcommSocketToServiceRecord(uuid)
                socket.connect()
                this.connected = true
            } catch (e: IllegalArgumentException) {
                ErrorNotification("Bluetooth device not found", activity)
                this.connected = false
            } catch (e: IOException) {
                e.printStackTrace()
                this.connected = false
            }
        }
    }

    /**
     * method is used to read incoming data from Hive
     * than data is processed and inserted to database
     * @param context of application
     */
    private fun read(context: Context) {
        val inputStream = socket.inputStream
        val buffer = ByteArray(1024)
        var bytes: Int
        while (true) {
            bytes = inputStream.read(buffer)
            val message = String(buffer, 0, bytes)
            if (message == "x") {
                inputStream.close()
                break;
            }
            if (bytes == -1) {
                inputStream.close()
                break;
            }
            val temp = message.substring(14, 18).toDouble()
            val weight = message.substring(19, 23).toDouble()
            val hiveTemp = message.substring(24, 28).toDouble()
            val humidity = message.substring(29, 31).toInt()
            try {
                Database(context).writableDatabase.execSQL(
                    "INSERT INTO MONITORING(title,date,temperature,weight,hiveTemp,humidity) VALUES(?,datetime('${
                        message.substring(
                            0,
                            13
                        )
                    }:00:00'),?,?,?,?)",
                    arrayOf(title, temp, weight, hiveTemp, humidity)
                )
            } catch (e: SQLiteConstraintException) {
                Toast.makeText(context, "Tieto dáta už boli načitané", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * function sends data to Hive
     * as follow read data from Hive and display them to Monitoring Fragment
     * @return ArrayList of Strings containing temperature in Hive, Humidity and Weight
     */
    fun sendLiveRqst(): ArrayList<String> {
        val result = ArrayList<String>()
        if (this.connected) {
            try {
                socket.outputStream.write("L".toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val inputStream = socket.inputStream
            val buffer = ByteArray(1024)
            var bytes: Int
            for (i in 0..2) {
                bytes = inputStream.read(buffer)
                val message = String(buffer, 0, bytes)
                result.add(message)
                Log.d(TAG, "live data are $message")
            }
        }
        return result
    }

    /**
     * Sends data to connected device via Bluetooth
     *
     * @param message determine to be send to Hive
     */
    fun sendData(message: String) {
        if (this.connected) {
            try {
                socket.outputStream.write(message.toByteArray())
                if (message == "R") {
                    this.read(this.context)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     *Method closes the socket
     */
    fun close() {
        if (::socket.isInitialized) {
            socket.close()
        }
        this.connected = false
    }
}