package com.bible.alive.utils

object Constants {

    object BibleVersions {
        const val RV1960 = "RVR1960"
        const val NTV = "NTV"
        const val NVI = "NVI"
        const val DEFAULT = RV1960

        val ALL_VERSIONS = listOf(RV1960, NTV, NVI)
    }

    object Notifications {
        const val CHANNEL_DAILY_VERSE = "daily_verse_channel"
        const val CHANNEL_STREAK = "streak_channel"
        const val NOTIFICATION_DAILY_VERSE_ID = 1001
        const val NOTIFICATION_STREAK_REMINDER_ID = 1002
        const val NOTIFICATION_STREAK_WARNING_ID = 1003
    }

    object WorkManager {
        const val DAILY_VERSE_WORK_NAME = "daily_verse_notification_work"
        const val STREAK_REMINDER_WORK_NAME = "streak_reminder_work"
        const val STREAK_CHECK_WORK_NAME = "streak_check_work"
        const val TAG_DAILY_VERSE = "tag_daily_verse"
        const val TAG_STREAK_REMINDER = "tag_streak_reminder"
    }

    object Preferences {
        const val PREFERENCES_NAME = "bible_alive_preferences"
        const val DEFAULT_NOTIFICATION_HOUR = 8
        const val DEFAULT_NOTIFICATION_MINUTE = 0
        const val STREAK_WARNING_HOUR = 20
        const val STREAK_WARNING_MINUTE = 0
    }

    object Reading {
        const val MINIMUM_VERSES_FOR_STREAK = 5
        const val VERSES_PER_PAGE = 50
    }

    object Api {
        const val BASE_URL = "https://bolls.life/api/"
        const val TIMEOUT_SECONDS = 30L
    }

    object Database {
        const val DATABASE_NAME = "bible_alive_database"
        const val CACHE_EXPIRY_DAYS = 7
    }

    object TextToSpeech {
        const val DEFAULT_PITCH = 1.0f
        const val DEFAULT_SPEECH_RATE = 0.9f
        const val ELDERLY_SPEECH_RATE = 0.75f
        const val YOUNG_SPEECH_RATE = 1.0f
    }

    object Streak {
        val MILESTONES = listOf(7, 14, 21, 30, 60, 90, 100, 180, 365)
    }

    object Intent {
        const val ACTION_OPEN_DAILY_VERSE = "com.bible.alive.ACTION_OPEN_DAILY_VERSE"
        const val ACTION_OPEN_READING = "com.bible.alive.ACTION_OPEN_READING"
        const val EXTRA_BOOK_NUMBER = "extra_book_number"
        const val EXTRA_CHAPTER_NUMBER = "extra_chapter_number"
    }
}
