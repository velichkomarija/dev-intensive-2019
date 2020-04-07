package ru.skillbranch.devintensive.utils

import android.util.Log

object Utils {

    fun parseFullName(fullName: String?): Pair<String?, String?> {

        if(fullName.isNullOrEmpty() || fullName?.trim().isNullOrEmpty())
            return Pair(null, null)

        val nameParts = fullName.split(" ")

        if (nameParts.size == 1)
            return Pair(nameParts[0], null)

        return Pair(nameParts[0], nameParts[1])
    }
}
