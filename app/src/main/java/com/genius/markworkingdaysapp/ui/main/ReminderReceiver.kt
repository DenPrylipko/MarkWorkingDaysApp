package com.genius.markworkingdaysapp.ui.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.genius.markworkingdaysapp.data.AppSettings
import com.genius.markworkingdaysapp.data.NotificationHelper
import com.genius.markworkingdaysapp.data.ReminderScheduler

class ReminderReceiver : BroadcastReceiver() {

    @SuppressLint("SuspiciousIndentation")
    override fun onReceive(context: Context, intent: Intent) {
        val settings = AppSettings(context)

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            if (settings.notificationsEnabled) {
                ReminderScheduler(context).schedule(
                    settings.reminderHour,
                    settings.reminderMinute
                )
            }
            return
        }

        if (!settings.todayChecked) {
            NotificationHelper(context).createChannel()
            NotificationHelper(context).showTestNotification()
        }

        ReminderScheduler(context).schedule(
            settings.reminderHour,
            settings.reminderMinute
        )
    }
}

