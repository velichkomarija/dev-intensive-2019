package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Расширения для класса Activity.
 */

/**
 * Метод для сокрытия клавиатуры.
 */
fun Activity.hideKeyboard()
{
    val methodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = this.currentFocus ?: View(this)
    methodManager.hideSoftInputFromWindow(view.windowToken, 0)
}