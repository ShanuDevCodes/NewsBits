package com.shanudevcodes.newsbits.data

import java.text.SimpleDateFormat
import java.util.*

fun formatDateString(rawDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC") // Parse assuming input is UTC
        }

        val outputFormat = SimpleDateFormat("dd MMMM yy, hh:mm a", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault() // Convert to device's local time zone
        }

        val date = inputFormat.parse(rawDate)
        outputFormat.format(date!!)
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