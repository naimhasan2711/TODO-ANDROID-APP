package com.nakibul.todo.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.nakibul.todo.R
import com.nakibul.todo.ui.activity.MainActivity
import com.nakibul.todo.utils.Constants
import com.nakibul.todo.utils.Converters

class ForegroundService: Service() {
    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
          val todoJsonItem = intent?.extras?.get(Constants.TODO_ITEM_KEY) as String
            val todo = Converters.jsonToGson(todoJsonItem)
            val title = todo.title
            val description = todo.todo.joinToString("\n")
            val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            //configure notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val mNotificationChannel = NotificationChannel(
                    Constants.NOTIFICATION_CHANNEL_ID,
                    Constants.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH
                )
                mNotificationChannel.description = "This is a test notification"
                mNotificationChannel.enableLights(true)
                mNotificationChannel.lightColor = Color.WHITE
                mNotificationChannel.enableVibration(true)
                mNotificationManager.createNotificationChannel(mNotificationChannel)
                mNotificationChannel.canBypassDnd()
            }

            //pending intent
            val intent = Intent(this, MainActivity::class.java)
            val taskStackBuilder = TaskStackBuilder.create(this)
            taskStackBuilder.addNextIntentWithParentStack(intent)
            val pendingIntent =
                taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

            val notification =
                NotificationCompat.Builder(applicationContext, Constants.NOTIFICATION_CHANNEL_ID)
                    .setContentTitle(title)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(description))
                    .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                    .setContentIntent(pendingIntent)
                    .build()
            startForeground(101, notification)
            return START_STICKY

    }

}