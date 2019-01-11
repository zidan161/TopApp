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

@SuppressLint("SimpleDateFormat")
fun Date.toSimpleString(): String {
    return SimpleDateFormat("EEE, dd-MM-yy").format(this)
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