package com.bible.alive.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bible.alive.BibleAliveApplication
import com.bible.alive.R
import com.bible.alive.domain.model.Verse
import com.bible.alive.ui.MainActivity
import com.bible.alive.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor() {

    fun showDailyVerseNotification(context: Context, verse: Verse) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            action = Constants.Intent.ACTION_OPEN_DAILY_VERSE
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Constants.Notifications.NOTIFICATION_DAILY_VERSE_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, BibleAliveApplication.CHANNEL_DAILY_VERSE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.daily_verse_title))
            .setContentText(verse.reference)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${verse.reference}\n\n${verse.text}")
                .setBigContentTitle(context.getString(R.string.daily_verse_title)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Constants.Notifications.NOTIFICATION_DAILY_VERSE_ID, notification)
    }

    fun showStreakReminderNotification(context: Context, currentStreak: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            action = Constants.Intent.ACTION_OPEN_READING
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Constants.Notifications.NOTIFICATION_STREAK_REMINDER_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = context.getString(R.string.streak_warning_message, currentStreak)

        val notification = NotificationCompat.Builder(context, BibleAliveApplication.CHANNEL_STREAK)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(context.getString(R.string.streak_warning_title))
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Constants.Notifications.NOTIFICATION_STREAK_REMINDER_ID, notification)
    }

    fun showStreakWarningNotification(context: Context, currentStreak: Int, hoursRemaining: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            action = Constants.Intent.ACTION_OPEN_READING
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            Constants.Notifications.NOTIFICATION_STREAK_WARNING_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "⚠️ ${context.getString(R.string.streak_warning_title)}"
        val message = "Quedan $hoursRemaining horas para mantener tu racha de $currentStreak días. ¡No la pierdas!"

        val notification = NotificationCompat.Builder(context, BibleAliveApplication.CHANNEL_STREAK)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Constants.Notifications.NOTIFICATION_STREAK_WARNING_ID, notification)
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }

    fun cancelAllNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    fun areNotificationsEnabled(context: Context): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationManager.areNotificationsEnabled()
        } else {
            true
        }
    }
}
