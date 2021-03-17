package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private var url: String? = null
    private var message = ""
    private lateinit var downloadManager: DownloadManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        createChannel(getString(R.string.notification_channel_id), getString(R.string.notification_channel_name))

        custom_button.setOnClickListener {
            when (radioGroup.checkedRadioButtonId) {
                R.id.glideRadioButton -> {
                    url = GLIDE_URL; message = getString(R.string.load_message)
                }
                R.id.loadAppRadioButton -> {
                    url = LOAD_APP_URL; message = getString(R.string.load_message)
                }
                R.id.retrofitRadioButton -> {
                    url = RETROFIT_URL; message = getString(R.string.retrofit_message)
                }
            }

            if (url != null) {
                download(url)
            } else {
                Toast.makeText(applicationContext, "Please select url", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id != null) {
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))
                while (cursor.moveToNext()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val successStatus = status == DownloadManager.STATUS_SUCCESSFUL
                    if (context != null) {
                        custom_button.finishDownload()
                        notificationManager.sendNotification(message, context, successStatus)
                    }
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = getColor(R.color.colorPrimary)
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


    private fun download(url: String?) {
        val request =
                DownloadManager.Request(Uri.parse(url))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
        custom_button.startDownload()
    }

    companion object {
        private const val GLIDE_URL =
                "https://github.com/bumptech/glide/archive/master.zip"

        private const val LOAD_APP_URL =
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private const val RETROFIT_URL =
                "https://github.com/square/retrofit/archive/master.zip"

        private const val CHANNEL_ID = "channelId"
    }

}
