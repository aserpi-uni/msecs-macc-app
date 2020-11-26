package it.uniroma1.keeptime.ui

import android.graphics.Color
import java.util.*

fun statusColor(status: String, deliveryDate: Date): Int {
    return when {
        status == "finished" -> Color.parseColor("#00b248")  // Green
        status == "undefined" -> Color.GRAY
        deliveryDate.before(Date()) -> Color.parseColor("#c62828")  // Red
        else -> Color.parseColor("#304ffe")  // Blue
    }
}
