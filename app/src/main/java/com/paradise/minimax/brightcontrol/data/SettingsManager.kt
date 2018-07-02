package com.paradise.minimax.brightcontrol.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager private constructor(private val context: Context) : SharedPreferences.OnSharedPreferenceChangeListener {

    interface Listener {
        fun onTransparencyChanged(value: Float)
    }

    companion object {
        val APP_PREFERENCES_NAME = "APP_SETTINGS"
        val ATR_OPACITY = "ATR_OPACITY"

        @Volatile
        private var INSTANCE: SettingsManager? = null

        fun getInstance(context: Context): SettingsManager =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: SettingsManager(context.applicationContext).also { INSTANCE = it }
                }
    }

    val sharedPreferences = context.getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE)

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    protected fun finalize() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }


    val listeners = mutableListOf<Listener>()

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private fun applyChangesToListeners(action: (Listener) -> Unit) {
        listeners.forEach(action)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

    }

    var opacity: Float
        get() {
            return sharedPreferences.getFloat(ATR_OPACITY, 1f)
        }
        set(value) {
            sharedPreferences.edit().putFloat(ATR_OPACITY, value).apply()
            applyChangesToListeners({ it.onTransparencyChanged(value) })
        }

}