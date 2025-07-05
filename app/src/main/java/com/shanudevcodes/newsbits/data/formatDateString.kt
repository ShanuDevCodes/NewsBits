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