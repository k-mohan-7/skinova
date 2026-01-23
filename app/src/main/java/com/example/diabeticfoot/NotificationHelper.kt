package com.example.diabeticfoot

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*

class NotificationHelper(private val context: Context) {
    
    companion object {
        const val CHANNEL_ID = "reminder_channel"
        const val CHANNEL_NAME = "Reminders"
        const val CHANNEL_DESCRIPTION = "Notifications for reminders"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder(reminderId: Int, title: String, date: String, time: String) {
        try {
            // Parse date and time with high precision
            val dateTimeString = "$date $time"
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            sdf.isLenient = false // Strict parsing
            val reminderDateTime = sdf.parse(dateTimeString)

            if (reminderDateTime != null) {
                val currentTime = System.currentTimeMillis()
                // Use exact time without any rounding
                val reminderTime = reminderDateTime.time

                if (reminderTime > currentTime) {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    
                    val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                        putExtra("reminder_id", reminderId)
                        putExtra("reminder_title", title)
                    }

                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        reminderId,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    // Use setExactAndAllowWhileIdle for precise timing
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            reminderTime,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            reminderTime,
                            pendingIntent
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showNotification(reminderId: Int, title: String) {
        // Check for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        // Create action intents
        val completeIntent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = "ACTION_COMPLETE"
            putExtra("reminder_id", reminderId)
            putExtra("reminder_title", title)
        }
        val completePendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId * 10 + 1,
            completeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = Intent(context, ReminderActionReceiver::class.java).apply {
            action = "ACTION_CANCEL"
            putExtra("reminder_id", reminderId)
            putExtra("reminder_title", title)
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId * 10 + 2,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Diabetic Foot Reminder")
            .setContentText(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .addAction(
                android.R.drawable.ic_menu_save,
                "Complete",
                completePendingIntent
            )
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Cancel",
                cancelPendingIntent
            )

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(reminderId, notificationBuilder.build())
    }

    fun cancelReminder(reminderId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}
