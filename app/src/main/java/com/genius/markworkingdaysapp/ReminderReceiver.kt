package com.genius.markworkingdaysapp

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.genius.markworkingdaysapp.data.NotificationHelper
import com.genius.markworkingdaysapp.data.ReminderScheduler
import com.genius.markworkingdaysapp.data.db.DatabaseProvider
import com.genius.markworkingdaysapp.data.repository.SettingsRepository
import com.genius.markworkingdaysapp.data.repository.WorkDayRepository
import com.genius.markworkingdaysapp.model.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class ReminderReceiver : BroadcastReceiver() {

    @SuppressLint("SuspiciousIndentation")
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val settings = SettingsRepository(context).settings.value

        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            try {
                if (settings.reminder.enabled) {
                    ReminderScheduler(context).schedule(
                        settings.reminder.hour,
                        settings.reminder.minute
                    )
                }
            } finally {
                pendingResult.finish()
            }
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dao = DatabaseProvider.get(context)
                val repository = WorkDayRepository(dao)

                val today = LocalDate.now()
                val isTodayChecked = repository.hasWorkDayEntry(today)

                if (!isTodayChecked) {
                    NotificationHelper(context).createChannel()
                    NotificationHelper(context).showTestNotification()
                }

                ReminderScheduler(context).schedule(
                    settings.reminder.hour,
                    settings.reminder.minute
                )

            } finally {
                pendingResult.finish()
            }
        }

    }
}