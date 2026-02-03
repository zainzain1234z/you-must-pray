package com.youmustpray

import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

object PrayerSchedule {
    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    data class PrayerTimesResult(
        val nextPrayer: String,
        val formattedSchedule: String
    )

    fun buildPrayerTimes(
        latitude: Double,
        longitude: Double,
        zoneId: ZoneId
    ): PrayerTimesResult {
        val coordinates = Coordinates(latitude, longitude)
        val params = CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
        params.madhab = Madhab.SHAFI

        val today = DateComponents.from(LocalDate.now(zoneId))
        val prayerTimes = PrayerTimes(coordinates, today, params)

        val schedule = listOf(
            "Fajr" to prayerTimes.fajr,
            "Sunrise" to prayerTimes.sunrise,
            "Dhuhr" to prayerTimes.dhuhr,
            "Asr" to prayerTimes.asr,
            "Maghrib" to prayerTimes.maghrib,
            "Isha" to prayerTimes.isha
        )

        val now = LocalDateTime.now(zoneId)
        val next = schedule.firstOrNull { (_, time) -> toLocalDateTime(time, zoneId).isAfter(now) }
            ?: ("Fajr" to prayerTimes.fajr)

        val nextPrayerLabel = "Next prayer: ${next.first} at ${formatter.format(toLocalDateTime(next.second, zoneId))}"
        val lines = schedule.joinToString("\n") { (name, time) ->
            val formatted = formatter.format(toLocalDateTime(time, zoneId))
            "$name  •  $formatted"
        }

        return PrayerTimesResult(nextPrayer = nextPrayerLabel, formattedSchedule = lines)
    }

    private fun toLocalDateTime(time: Date, zoneId: ZoneId): LocalDateTime {
        return time.toInstant().atZone(zoneId).toLocalDateTime()
    }
}
