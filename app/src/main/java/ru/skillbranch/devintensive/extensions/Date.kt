package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*

const val SECONDS = 1000L
const val MINUTES = 60 * SECONDS
const val HOUR = 60 * MINUTES
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val simpleDateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return simpleDateFormat.format(this)
}

fun Date.add(value: Int, unit: TimeUnits = TimeUnits.SECOND): Date {
    var time = this.time

    time += when (unit) {
        TimeUnits.SECOND -> value * SECONDS
        TimeUnits.MINUTE -> value * MINUTES
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }

    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    var difference = date.time - this.time

    fun dif(type: Long, forOne: String = "", forTwoToFour: String = "", other: String = ""): String {

        if (difference < 0) {
            difference = -difference
        }

        return when {
            difference / type in 5..20 -> other
            (difference / type) % 10 == 1L -> forOne
            (difference / type) % 10 in 2..4 -> forTwoToFour
            else -> other
        }
    }

    if (difference < 0) {
        return when (-difference) {
            in 0..1 * SECONDS -> "только что"
            in 1 * SECONDS..45 * SECONDS -> "через несколько секунд"
            in 45 * SECONDS..75 * SECONDS -> "через минуту"
            in 75 * SECONDS..45 * MINUTES -> "через ${-difference / MINUTES}" +
                    " ${dif(MINUTES, "минуту", "минуты", "минут")}"
            in 45 * MINUTES..75 * MINUTES -> "через час"
            in 75 * MINUTES..22 * HOUR -> "через ${-difference / HOUR}" +
                    " ${dif(HOUR, "час", "часа", "часов")}"
            in 22 * HOUR..26 * HOUR -> "через день"
            in 26 * HOUR..360 * DAY -> "через ${-difference / DAY}" +
                    " ${dif(DAY, "день", "дня", "дней")}"
            else -> "более чем через год"
        }
    } else {
        return when (difference) {
            in 0..1 * SECONDS -> "только что"
            in 1 * SECONDS..45 * SECONDS -> "несколько секунд назад"
            in 45 * SECONDS..75 * SECONDS -> "минуту назад"
            in 75 * SECONDS..45 * MINUTES -> "${difference / MINUTES}" +
                    " ${dif(MINUTES, "минуту", "минуты", "минут")} назад"
            in 45 * MINUTES..75 * MINUTES -> "час назад"
            in 75 * MINUTES..22 * HOUR -> "${difference / HOUR}" +
                    " ${dif(HOUR, "час", "часа", "часов")} назад"
            in 22 * HOUR..26 * HOUR -> "день назад"
            in 26 * HOUR..360 * DAY -> "${difference / DAY}" +
                    " ${dif(DAY, "день", "дня", "дней")} назад"
            else -> "более года назад"
        }
    }
}

enum class TimeUnits(val ONE: String, val FEW: String, val MANY: String) {

    SECOND("секунду", "секунды", "секунд"),
    MINUTE("минуту", "минуты", "минут"),
    HOUR("час", "часа", "часов"),
    DAY("день", "дня", "дней");

    fun plural(num: Int): String {
        return "$num ${this.getAmount(num)}"
    }

    private fun getAmount(num: Int): String {
        return when {
            num in 5..20 -> MANY
            num % 10 == 1 -> ONE
            num % 10 in 2..4 -> FEW
            else -> MANY
        }
    }
}