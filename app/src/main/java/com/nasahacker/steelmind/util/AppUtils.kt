package com.nasahacker.steelmind.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppUtils {
    fun openLink(context: Context, link: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }

    fun getCurrentDate(): String {
        val milliSeconds = System.currentTimeMillis()
        val date = Date(milliSeconds)
        val formatter = SimpleDateFormat("hh:mm:ss a dd/MM/yyyy", Locale.getDefault())
        return formatter.format(date)
    }


    fun getTimeHMS(milliSeconds: Long): String {
        // Calculate the time difference from the provided timestamp to now
        val elapsedTimeInMillis = System.currentTimeMillis() - milliSeconds

        // Calculate hours, minutes, and seconds for the total elapsed time
        val hours = (elapsedTimeInMillis / (60 * 60 * 1000)) % 24
        val minutes = (elapsedTimeInMillis / (60 * 1000)) % 60
        val seconds = (elapsedTimeInMillis / 1000) % 60

        // Format the elapsed time as "HH:mm:ss"
        return String.format(
            Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds
        )
    }


    fun getDayProgressPercentage(): Int {
        // Current time in milliseconds
        val currentTimeMillis = System.currentTimeMillis()

        // Total milliseconds in a day
        val maxMillisInDay = 24 * 60 * 60 * 1000L

        // Calculate the time elapsed since the start time
        val millisSinceStart = currentTimeMillis - MmkvManager.getStartTime()

        // Get only the elapsed milliseconds of the current day
        val millisToday = millisSinceStart % maxMillisInDay

        // Calculate the percentage of the current day that has passed
        val percentage = (millisToday.toDouble() / maxMillisInDay * 100).toInt()

        return percentage
    }


    fun getTimeDD(milliSeconds: Long): String {
        val elapsedTime = System.currentTimeMillis() - milliSeconds

        // Calculate the number of full days
        val days = elapsedTime / (24 * 60 * 60 * 1000)

        return if (days < 365) {
            // If less than 365 days, show days without leading zeros
            "$days days"
        } else {
            // If 365 days or more, calculate years and remaining days
            val years = days / 365
            val remainingDays = days % 365

            // Dynamically display remainingDays without unnecessary leading zeros
            String.format(
                Locale.getDefault(),
                "%d year%s, %d day%s",
                years,
                if (years > 1) "s" else "",
                remainingDays,
                if (remainingDays != 1L) "s" else ""
            )
        }
    }
}
