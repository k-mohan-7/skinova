package com.example.diabeticfoot

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getIntExtra("reminder_id", 0)
        val action = intent.action
        
        val cloudUserManager = CloudUserManager(context)
        
        when (action) {
            "ACTION_COMPLETE" -> {
                // Mark reminder as completed
                CoroutineScope(Dispatchers.IO).launch {
                    cloudUserManager.updateReminderStatusV2(reminderId, "completed").onSuccess {
                        Log.d("ReminderAction", "Reminder $reminderId marked as completed")
                    }
                }
                
                // Dismiss notification
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(reminderId)
                
                // Show toast
                android.widget.Toast.makeText(
                    context,
                    "Reminder completed!",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
            "ACTION_CANCEL" -> {
                // Mark reminder as cancelled
                CoroutineScope(Dispatchers.IO).launch {
                    cloudUserManager.updateReminderStatusV2(reminderId, "cancelled").onSuccess {
                        Log.d("ReminderAction", "Reminder $reminderId marked as cancelled")
                    }
                }
                
                // Dismiss notification
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(reminderId)
                
                // Show toast
                android.widget.Toast.makeText(
                    context,
                    "Reminder cancelled",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
