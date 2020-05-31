package ru.skillbranch.devintensive.extensions

import androidx.lifecycle.MutableLiveData

/**
 * Расширение для приведения к  MutableLiveData<T>.
 */
fun <T> mutableLiveData(defaultValue: T? = null):
        MutableLiveData<T> {

    val data = MutableLiveData<T>()

    if (defaultValue != null) {
        data.value = defaultValue
    }

    return data
}