package com.paradise.minimax.brightcontrol.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager private constructor(
        context: Context)
    : SharedPreferences.OnSharedPreferenceChangeListener {

    interface Listener {
        fun onTransparencyChanged(value: Float)
    }

    companion object {
        const val APP_PREFERENCES_NAME = "APP_SETTINGS"

        const val OPACITY_ATR_KEY = "OPACITY_ATR_KEY"
        const val OPACITY_DEFAULT_VALUE = 1f

        const val START_ON_BOOT_ATR_KEY = "START_ON_BOOT"
        const val START_ON_BOOT_DEFAULT_VALUE = false

        @Volatile
        private var INSTANCE: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: SettingsManager(context.applicationContext).also { INSTANCE = it }
                }
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val listeners = mutableListOf<Listener>()

    fun addListener(listener: Listener) {
        if (listeners.isEmpty()) {
            sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
        if (listeners.isEmpty()) {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }
    }

    private fun applyChangesToListeners(action: (Listener) -> Unit) {
        listeners.forEach(action)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            OPACITY_ATR_KEY -> applyChangesToListeners { it.onTransparencyChanged(sharedPreferences.getFloat(OPACITY_ATR_KEY, OPACITY_DEFAULT_VALUE)) }
        }
    }

    var startOnBoot: Boolean
        get() {
            return sharedPreferences.getBoolean(START_ON_BOOT_ATR_KEY, START_ON_BOOT_DEFAULT_VALUE)
        }
        set(value) {
            sharedPreferences.edit().putBoolean(START_ON_BOOT_ATR_KEY, value).apply()
        }

    var opacity: Float
        get() {
            return sharedPreferences.getFloat(OPACITY_ATR_KEY, OPACITY_DEFAULT_VALUE)
        }
        set(value) {
            sharedPreferences.edit().putFloat(OPACITY_ATR_KEY, value).apply()
        }

}