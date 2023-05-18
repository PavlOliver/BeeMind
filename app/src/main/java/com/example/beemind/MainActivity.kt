package com.example.beemind

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.beemind.databaseData.Database
import com.example.beemind.databaseData.Tasks
import com.example.beemind.databinding.ActivityMainBinding
import com.google.android.gms.location.LocationServices

/**
 * The main activity of the application.
 * This activity serves as the entry point of the application and provides the main user interface.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Create a view of the entry point of application
     *
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (ContextCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION),
                123
            )
        } else {
            var ownerName = ""
            val cursor = Database(this).readableDatabase.rawQuery(
                "SELECT owner,lat,lon FROM OWNER",
                null
            )
            val apiaryLocation = Location("Apiary")
            if (cursor.moveToFirst()) {
                val ownerIndex = cursor.getColumnIndex("owner")
                ownerName = cursor.getString(ownerIndex).substringBefore(" ")

                val latIndex = cursor.getColumnIndex("lat")
                val lonIndex = cursor.getColumnIndex("lon")
                //49.209415624030136,18.75848378828301
                apiaryLocation.latitude = cursor.getDouble(latIndex)
                apiaryLocation.longitude = cursor.getDouble(lonIndex)
            }
            cursor.close()
            val notificationManager = NotificationManagerCompat.from(applicationContext)
            val notificationChannel = NotificationChannel(
                "A",
                "My Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
            val builder = NotificationCompat.Builder(applicationContext, "A")
                .setSmallIcon(R.drawable.icon)
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "Here are your tasks:\n" +
                                "${Tasks.get(this)}"
                    )
                )
                .setContentTitle("Hi $ownerName, looks like u are located at apiary")

            val locationClient =
                LocationServices.getFusedLocationProviderClient(applicationContext)
            locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val distance = location.distanceTo(apiaryLocation) / 1000
                    Toast.makeText(
                        applicationContext,
                        "distance is $distance",
                        Toast.LENGTH_LONG
                    ).show()

                    if (distance < 0.02) {
                        notificationManager.notify(1, builder.build())
                        Tasks.delete(this)
                    }
                }
            }
            locationClient.lastLocation.addOnFailureListener {
                Toast.makeText(applicationContext, "No GPS in device", Toast.LENGTH_LONG)
                    .show()

            }
        }
    }
}