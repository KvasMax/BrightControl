package com.paradise.minimax.brightcontrol.view

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.SeekBar
import com.paradise.minimax.brightcontrol.data.SettingsManager
import kotlin.math.min


class WindowManipulator(context: Context) : SettingsManager.Listener {

    private val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    private val screenSize: Point = Point().also { windowManager.defaultDisplay.getSize(it) }
    private val statusBarHeight: Int = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            .takeIf { it > 0 }?.let { context.resources.getDimensionPixelSize(it) } ?: 0
    private val appContext = context.applicationContext
    private val seekBar = VerticalSeekBar(appContext)
    private val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM,
            PixelFormat.TRANSLUCENT)

    init {
        seekBar.max = 255
        params.gravity = Gravity.RIGHT or Gravity.BOTTOM
        params.x = 0
        params.y = 0
        params.height = min(screenSize.x, screenSize.y) - statusBarHeight

        val settingsManager = SettingsManager.getInstance(appContext)
        seekBar.alpha = settingsManager.opacity

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {

                Settings.System.putInt(appContext.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS, progress)

                Settings.System.putInt(appContext.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}

        })

    }

    fun showViews() {
        val brightness = Settings.System.getInt(appContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0)
        seekBar.progress = brightness
        windowManager.addView(seekBar, params)
        SettingsManager.getInstance(appContext).addListener(this)
    }

    fun removeViews() {
        SettingsManager.getInstance(appContext).removeListener(this)
        windowManager.removeView(seekBar)
    }

    override fun onTransparencyChanged(value: Float) {
        seekBar.alpha = value
    }
}
