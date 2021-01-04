package com.zidan.topapp

import android.annotation.SuppressLint
import android.view.View
import java.text.SimpleDateFormat
import java.util.*

fun View.visible(){
    visibility = View.VISIBLE
}

fun View.invisible(){
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun getPeriod(): Int {
    val cal = Calendar.getInstance()
    val day = cal.get(Calendar.DAY_OF_MONTH)
    return when (day) {
        in 1..7 -> 1
        in 8..15 -> 2
        in 16..22 -> 3
        else -> 4
    }
}

@SuppressLint("SimpleDateFormat")
fun Date.toSimpleString(): String {
    return SimpleDateFormat("EEE, dd MMM yyyy").format(this)
}

@SuppressLint("SimpleDateFormat")
fun Date.toMonth(): String {
    return SimpleDateFormat("MMMM").format(this)
}

@SuppressLint("SimpleDateFormat")
fun Date.toYear(): String {
    return SimpleDateFormat("yyyy").format(this)
}

fun toFormatString(text: String, format: String) = String.format(format, text)