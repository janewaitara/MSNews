package com.example.msnews.data.utils

import java.text.SimpleDateFormat
import java.util.*

object ExtensionFunctions {

    fun String.toFormattedDateAndTime(time: String): String {

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = dateFormatter.parse(time)

        val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = timeFormatter.format(date!!)

        val dayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // val dayFormatter = SimpleDateFormat("dd EEE yyyy", Locale.getDefault())
        val formattedDate = dayFormatter.format(date)

        return "$formattedDate at $formattedTime"
        // return timeFormatter.format(date!!)
        // return date!!.toString()
    }
}
