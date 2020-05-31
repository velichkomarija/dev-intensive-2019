package ru.skillbranch.devintensive

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import ru.skillbranch.devintensive.repositories.PreferencesRepository

/**
 * Надстройка приложения.
 */
class App : Application() {

    /**
     * Объект, предоставляющий контекст.
     */
    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    /**
     * Инициализатор класса.
     */
    init {
        instance = this
    }

    /**
     * Переопределение метода жизненного цикла.
     */
    override fun onCreate() {
        super.onCreate()
        PreferencesRepository.getAppTheme()
                .also {
                    AppCompatDelegate.setDefaultNightMode(it)
                }
    }
}