package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun Date.format(pattern:  String = "HH:mm:ss dd.MM.yy"): String {
    val simpleDateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return simpleDateFormat.format(this)
}

fun Date.add(value: Int, unit: TimeUnit): Date {
    return this.add(value, unit)
}