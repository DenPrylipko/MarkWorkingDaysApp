package com.genius.markworkingdaysapp.ui.xml.main

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.genius.markworkingdaysapp.data.AppSettings
import com.genius.markworkingdaysapp.data.NotificationHelper
import com.genius.markworkingdaysapp.data.ReminderScheduler
import com.genius.markworkingdaysapp.data.db.DatabaseProvider
import com.genius.markworkingdaysapp.data.db.WorkDayRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class ReminderReceiver : BroadcastReceiver() {

    @SuppressLint("SuspiciousIndentation")
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val settings = AppSettings(context)

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            try {
                if (settings.notificationsEnabled) {
                    ReminderScheduler(context).schedule(
                        settings.reminderHour,
                        settings.reminderMinute
                    )
                }
            } finally {
                pendingResult.finish()
            }
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = DatabaseProvider.get(context).workDayDao()
                val repository = WorkDayRepository(dao)

                val today = LocalDate.now()
                val isTodayChecked = repository.isDayChecked(today)

                if (!isTodayChecked) {
                    NotificationHelper(context).createChannel()
                    NotificationHelper(context).showTestNotification()
                }

                ReminderScheduler(context).schedule(
                    settings.reminderHour,
                    settings.reminderMinute
                )

            } finally {
                pendingResult.finish()
            }
        }

    }
}

