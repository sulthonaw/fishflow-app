package com.fishflow.app.core.util

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone


object DateFormatter {
    private val indonesiaLocale = Locale.forLanguageTag("id-ID")
    private val isoLocale = Locale.US

    private val isoPatterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd"
    )

    fun formatDate(date: Date?): String {
        if (date == null) return "-"
        return createFormatter(Constants.DatePatterns.DATE).format(date)
    }

    fun formatDateTime(date: Date?): String {
        if (date == null) return "-"
        return createFormatter(Constants.DatePatterns.DATE_TIME).format(date)
    }

    /** Parse timestamp ISO 8601 dari backend/Supabase menjadi Date. */
    fun parseIsoDate(value: String?): Date? {
        val text = value?.trim().orEmpty()
        if (text.isEmpty()) return null

        for (pattern in isoPatterns) {
            val parser = SimpleDateFormat(pattern, isoLocale).apply {
                isLenient = false
                // Jika timestamp memakai akhiran literal Z, artinya UTC.
                if (pattern.endsWith("'Z'")) {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
            }

            val position = ParsePosition(0)
            val parsedDate = parser.parse(text, position)
            if (parsedDate != null && position.index == text.length) {
                return parsedDate
            }
        }

        return null
    }

    fun formatIsoDate(value: String?): String {
        return formatDate(parseIsoDate(value))
    }

    fun formatIsoDateTime(value: String?): String {
        return formatDateTime(parseIsoDate(value))
    }

    fun formatRelative(date: Date?, now: Date = Date()): String {
        if (date == null) return "-"

        val target = Calendar.getInstance().apply { time = date }
        val today = Calendar.getInstance().apply { time = now }

        if (isSameDay(target, today)) return "Hari ini"

        val yesterday = (today.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        if (isSameDay(target, yesterday)) return "Kemarin"

        val tomorrow = (today.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, 1)
        }
        if (isSameDay(target, tomorrow)) return "Besok"

        return formatDate(date)
    }

    private fun createFormatter(pattern: String): SimpleDateFormat {
        return SimpleDateFormat(pattern, indonesiaLocale).apply {
            isLenient = false
        }
    }

    private fun isSameDay(first: Calendar, second: Calendar): Boolean {
        return first.get(Calendar.ERA) == second.get(Calendar.ERA) &&
            first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
            first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR)
    }
}
