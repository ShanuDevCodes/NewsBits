package com.shanudevcodes.newsbits.data

import java.text.SimpleDateFormat
import java.util.*

fun formatDateString(rawDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC") // Parse as UTC
        }

        val date = inputFormat.parse(rawDate) ?: return rawDate
        val now = Date()

        val diffMillis = now.time - date.time
        val diffDays = diffMillis / (24 * 60 * 60 * 1000)

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        when {
            // Today → exact time
            diffDays == 0L -> "${timeFormat.format(date)}"

            // Within 1 month
            diffDays in 1..30 -> "${diffDays} days ago"

            // Older than 1 month
            else -> {
                val outputFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
                outputFormat.format(date)
            }
        }
    } catch (e: Exception) {
        rawDate // Fallback if parsing fails
    }
}
fun getTimeAgo(rawDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val parsedDate = inputFormat.parse(rawDate) ?: return rawDate
        val timeInMillis = parsedDate.time
        val now = System.currentTimeMillis()
        val diff = now - timeInMillis

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes minute${if (minutes == 1L) "" else "s"} ago"
            hours < 24 -> "$hours hour${if (hours == 1L) "" else "s"} ago"
            days < 7 -> "$days day${if (days == 1L) "" else "s"} ago"
            else -> {
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).apply {
                    timeZone = TimeZone.getDefault()
                }
                outputFormat.format(parsedDate)
            }
        }
    } catch (e: Exception) {
        rawDate
    }
}