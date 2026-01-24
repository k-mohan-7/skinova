package com.example.diabeticfoot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("reminder_id", 0)
        val reminderTitle = intent.getStringExtra("reminder_title") ?: "Reminder"
        
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(reminderId, reminderTitle)
    }
}
