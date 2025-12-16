package com.bible.alive

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BibleAliveApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            val verseChannel = NotificationChannel(
                CHANNEL_DAILY_VERSE,
                getString(R.string.notification_channel_verse),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones del versículo diario"
            }

            val streakChannel = NotificationChannel(
                CHANNEL_STREAK,
                getString(R.string.notification_channel_streak),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recordatorios de racha de lectura"
            }

            notificationManager.createNotificationChannel(verseChannel)
            notificationManager.createNotificationChannel(streakChannel)
        }
    }

    companion object {
        const val CHANNEL_DAILY_VERSE = "daily_verse_channel"
        const val CHANNEL_STREAK = "streak_channel"
    }
}
