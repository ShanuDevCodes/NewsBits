package com.shanudevcodes.newsbits.data

import java.text.SimpleDateFormat
import java.util.*

fun formatDateString(rawDate: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yy, hh:mm a", Locale.getDefault())

        val date = inputFormat.parse(rawDate)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        rawDate // Fallback: return original if parsing fails
    }
}