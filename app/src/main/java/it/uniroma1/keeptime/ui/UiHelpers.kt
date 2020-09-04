package it.uniroma1.keeptime.ui

import android.graphics.Color
import java.util.*

fun statusColor(status: String, deliveryDate: Date): Int {
    return when {
        status == "finished" -> Color.GREEN
        status == "undefined" -> Color.GRAY
        deliveryDate.before(Date()) -> Color.RED
        else -> Color.GREEN
    }
}
