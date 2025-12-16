package com.bible.alive.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bible.alive.data.repository.StreakRepository
import com.bible.alive.data.repository.UserPreferencesRepository
import com.bible.alive.utils.Constants
import com.bible.alive.utils.DateUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class StreakReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val streakRepository: StreakRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val notificationsEnabled = userPreferencesRepository.notificationsEnabled.first()
            if (!notificationsEnabled) {
                return Result.success()
            }

            val hasReadToday = streakRepository.hasReadToday()
            if (hasReadToday) {
                return Result.success()
            }

            val streak = streakRepository.getStreak()
            if (streak.currentStreak > 0) {
                val hoursRemaining = DateUtils.getHoursUntilEndOfDay()
                if (hoursRemaining <= 4) {
                    notificationHelper.showStreakWarningNotification(
                        context, 
                        streak.currentStreak, 
                        hoursRemaining
                    )
                } else {
                    notificationHelper.showStreakReminderNotification(
                        context, 
                        streak.currentStreak
                    )
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun schedule(context: Context) {
            val delay = DateUtils.getMillisUntilNextAlarm(
                Constants.Preferences.STREAK_WARNING_HOUR,
                Constants.Preferences.STREAK_WARNING_MINUTE
            )

            val workRequest = PeriodicWorkRequestBuilder<StreakReminderWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(Constants.WorkManager.TAG_STREAK_REMINDER)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.WorkManager.STREAK_REMINDER_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        fun scheduleCheck(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<StreakReminderWorker>(
                4, TimeUnit.HOURS
            )
                .addTag(Constants.WorkManager.TAG_STREAK_REMINDER)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.WorkManager.STREAK_CHECK_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(Constants.WorkManager.STREAK_REMINDER_WORK_NAME)
            WorkManager.getInstance(context).cancelUniqueWork(Constants.WorkManager.STREAK_CHECK_WORK_NAME)
        }
    }
}
