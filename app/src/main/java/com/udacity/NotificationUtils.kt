package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, success: Boolean) {
    val resultIntent = Intent(applicationContext, DetailActivity::class.java)
    resultIntent.putExtra(applicationContext.getString(R.string.download_key), messageBody)
    resultIntent.putExtra(applicationContext.getString(R.string.success), success)

    val pendingIntent = TaskStackBuilder.create(applicationContext).run {
        addNextIntentWithParentStack(resultIntent)
        getPendingIntent(REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    val builder = NotificationCompat.Builder(applicationContext, applicationContext.getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.ic_baseline_cloud_download_24)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(messageBody)
            .addAction(
                    R.drawable.ic_baseline_cloud_download_24,
                    applicationContext.getString(R.string.check),
                    pendingIntent
            )

    notify(NOTIFICATION_ID, builder.build())
}