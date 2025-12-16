package com.bible.alive.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bible.alive.utils.Constants
import com.bible.alive.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "bible_alive_preferences")

class BootReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        scope.launch {
            try {
                rescheduleNotifications(context)
            } catch (e: Exception) {
                scheduleWithDefaults(context)
            }
        }
    }

    private suspend fun rescheduleNotifications(context: Context) {
        val notificationsEnabled = context.dataStore.data
            .map { preferences ->
                preferences[booleanPreferencesKey("notifications_enabled")] ?: true
            }
            .first()

        if (!notificationsEnabled) return

        val reminderTime = context.dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey("daily_reminder_time")] ?: "08:00"
            }
            .first()

        val (hour, minute) = DateUtils.parseTimeString(reminderTime)

        NotificationWorker.schedule(context, hour, minute)
        StreakReminderWorker.schedule(context)
        StreakReminderWorker.scheduleCheck(context)
    }

    private fun scheduleWithDefaults(context: Context) {
        NotificationWorker.schedule(
            context,
            Constants.Preferences.DEFAULT_NOTIFICATION_HOUR,
            Constants.Preferences.DEFAULT_NOTIFICATION_MINUTE
        )
        StreakReminderWorker.schedule(context)
    }
}
