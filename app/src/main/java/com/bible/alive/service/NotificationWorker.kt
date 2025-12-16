package com.bible.alive.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.bible.alive.data.repository.BibleRepository
import com.bible.alive.data.repository.UserPreferencesRepository
import com.bible.alive.domain.model.Result
import com.bible.alive.utils.Constants
import com.bible.alive.utils.DateUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bibleRepository: BibleRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val notificationsEnabled = userPreferencesRepository.notificationsEnabled.first()
            if (!notificationsEnabled) {
                return Result.success()
            }

            val translation = userPreferencesRepository.preferredTranslation.first()
            when (val result = bibleRepository.getRandomVerse(translation)) {
                is com.bible.alive.domain.model.Result.Success -> {
                    notificationHelper.showDailyVerseNotification(context, result.data)
                    Result.success()
                }
                is com.bible.alive.domain.model.Result.Error -> {
                    Result.retry()
                }
                is com.bible.alive.domain.model.Result.Loading -> {
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun schedule(context: Context, hour: Int, minute: Int) {
            val delay = DateUtils.getMillisUntilNextAlarm(hour, minute)

            val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(Constants.WorkManager.TAG_DAILY_VERSE)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                Constants.WorkManager.DAILY_VERSE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(Constants.WorkManager.DAILY_VERSE_WORK_NAME)
        }
    }
}
