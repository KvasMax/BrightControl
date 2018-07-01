package com.paradise.minimax.brightcontrol.view

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.SeekBar


class WindowManipulator(context: Context) {

    private val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    private val appContext = context.applicationContext
    private val seekBar = SeekBar(appContext)
    private val params = WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT)

    init {
        seekBar.max = 255
        params.gravity = Gravity.START
        params.x = 0
        params.y = 0
        params.width = 400

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, b: Boolean) {

                Settings.System.putInt(appContext.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS, progress)

                Settings.System.putInt(appContext.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }

        })

    }

    public fun showViews() {
        val brightness = Settings.System.getInt(appContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0)
        seekBar.progress = brightness
        windowManager.addView(seekBar, params)
    }

    public fun removeViews() {
        windowManager.removeView(seekBar)
    }

}