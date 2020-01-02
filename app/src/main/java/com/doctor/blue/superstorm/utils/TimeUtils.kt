package com.doctor.blue.superstorm.utils

import java.math.RoundingMode
import java.text.DecimalFormat

object TimeUtils {
    fun formateMilliSeccond(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60))
        val minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000)

        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }

        return "$finalTimerString$minutes:$secondsString"
    }

    fun formatSongProgress(time: Double): Int {
        val df = DecimalFormat("#")
        df.roundingMode = RoundingMode.CEILING
        return df.format(time).toInt()
    }
}