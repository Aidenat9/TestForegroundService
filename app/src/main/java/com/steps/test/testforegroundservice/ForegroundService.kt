package com.steps.test.testforegroundservice

import android.app.*
import android.app.NotificationManager.*
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

open class ForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val pendingIntent: PendingIntent = Intent(
                this,
                MainActivity::class.java
        ).let { notificationIntent->
            PendingIntent.getActivity(
                    this, 0, notificationIntent, getPendIntentFlag(PendingIntent.FLAG_UPDATE_CURRENT)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel("test", "fix exception", IMPORTANCE_HIGH)
            mChannel.description = "fix exception desc"
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            val notification: Notification = NotificationCompat.Builder(this, "test")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle("fix exception").setContentText("fix ForegroundServiceStartNotAllowedException").setContentIntent(pendingIntent)
                .setGroup(BuildConfig.APPLICATION_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build()
            startForeground(1, notification)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? = null

    open fun getPendIntentFlag(flag: Int): Int {
        return if (Build.VERSION.SDK_INT < 23) {
            flag
        } else PendingIntent.FLAG_IMMUTABLE
    }
}