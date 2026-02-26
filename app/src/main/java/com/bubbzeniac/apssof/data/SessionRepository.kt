package com.bubbzeniac.apssof.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

private val Context.sessionStore: DataStore<Preferences> by preferencesDataStore(name = "sessions")

data class DayProgress(
    val label: String,
    val minutes: Int,
    val isToday: Boolean = false,
)

data class ProgressData(
    val todayMinutes: Int = 0,
    val weekData: List<DayProgress> = emptyList(),
    val totalMinutes: Int = 0,
    val bestDayMinutes: Int = 0,
)

class SessionRepository(private val context: Context) {

    private fun dayKey(daysAgo: Int = 0): Preferences.Key<Int> {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        val dayEpoch = cal.timeInMillis / (1000L * 60 * 60 * 24)
        return intPreferencesKey("day_$dayEpoch")
    }

    private fun dayLabel(daysAgo: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Mon"
            Calendar.TUESDAY -> "Tue"
            Calendar.WEDNESDAY -> "Wed"
            Calendar.THURSDAY -> "Thu"
            Calendar.FRIDAY -> "Fri"
            Calendar.SATURDAY -> "Sat"
            Calendar.SUNDAY -> "Sun"
            else -> "?"
        }
    }

    val progressData: Flow<ProgressData> = context.sessionStore.data.map { prefs ->
        val days = (0..6).map { daysAgo ->
            val mins = prefs[dayKey(daysAgo)] ?: 0
            DayProgress(
                label = if (daysAgo == 0) "Today" else dayLabel(daysAgo),
                minutes = mins,
                isToday = daysAgo == 0
            )
        }
        val todayMins = days.first().minutes
        val totalMins = days.sumOf { it.minutes }
        val bestDay = days.maxOfOrNull { it.minutes } ?: 0
        ProgressData(
            todayMinutes = todayMins,
            weekData = days.reversed(),
            totalMinutes = totalMins,
            bestDayMinutes = bestDay,
        )
    }

    suspend fun addMinutesToday(minutes: Int) {
        if (minutes <= 0) return
        context.sessionStore.edit { prefs ->
            val key = dayKey(0)
            val current = prefs[key] ?: 0
            prefs[key] = current + minutes
        }
    }

    suspend fun clearAllProgress() {
        context.sessionStore.edit { prefs ->
            (0..6).forEach { daysAgo ->
                prefs[dayKey(daysAgo)] = 0
            }
        }
    }
}
