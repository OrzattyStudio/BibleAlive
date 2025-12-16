package com.bible.alive.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("d 'de' MMMM", Locale("es", "ES"))
    private val displayFullDateFormat = SimpleDateFormat("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))

    fun getTodayDateString(): String {
        return dateFormat.format(Date())
    }

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun formatDisplayDate(timestamp: Long): String {
        return displayDateFormat.format(Date(timestamp))
    }

    fun formatFullDisplayDate(timestamp: Long): String {
        return displayFullDateFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun getDayOfWeek(timestamp: Long): String {
        return dayOfWeekFormat.format(Date(timestamp)).replaceFirstChar { it.uppercase() }
    }

    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    fun isToday(timestamp: Long): Boolean {
        val todayStart = getStartOfDay()
        val todayEnd = getEndOfDay()
        return timestamp in todayStart..todayEnd
    }

    fun isYesterday(timestamp: Long): Boolean {
        val yesterdayStart = getStartOfDay() - TimeUnit.DAYS.toMillis(1)
        val yesterdayEnd = getEndOfDay() - TimeUnit.DAYS.toMillis(1)
        return timestamp in yesterdayStart..yesterdayEnd
    }

    fun daysBetween(startTimestamp: Long, endTimestamp: Long): Long {
        return TimeUnit.MILLISECONDS.toDays(endTimestamp - startTimestamp)
    }

    fun daysSince(timestamp: Long): Long {
        return daysBetween(timestamp, System.currentTimeMillis())
    }

    fun addDays(timestamp: Long, days: Int): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestamp
            add(Calendar.DAY_OF_YEAR, days)
        }.timeInMillis
    }

    fun getRelativeDateString(timestamp: Long): String {
        return when {
            isToday(timestamp) -> "Hoy"
            isYesterday(timestamp) -> "Ayer"
            daysSince(timestamp) < 7 -> getDayOfWeek(timestamp)
            else -> formatDisplayDate(timestamp)
        }
    }

    fun parseTimeString(time: String): Pair<Int, Int> {
        val parts = time.split(":")
        return if (parts.size == 2) {
            Pair(parts[0].toIntOrNull() ?: 8, parts[1].toIntOrNull() ?: 0)
        } else {
            Pair(8, 0)
        }
    }

    fun getMillisUntilNextAlarm(hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val alarm = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (alarm.before(now)) {
            alarm.add(Calendar.DAY_OF_YEAR, 1)
        }

        return alarm.timeInMillis - now.timeInMillis
    }

    fun getHoursUntilEndOfDay(): Int {
        val now = Calendar.getInstance()
        val endOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
        }
        return ((endOfDay.timeInMillis - now.timeInMillis) / (1000 * 60 * 60)).toInt()
    }
}
