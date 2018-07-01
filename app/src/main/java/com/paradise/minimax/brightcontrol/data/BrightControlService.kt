package com.paradise.minimax.brightcontrol.data

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.paradise.minimax.brightcontrol.view.WindowManipulator

class BrightControlService : Service() {

    private val windowManipulator by lazy { WindowManipulator(this) }

    override fun onCreate() {
        super.onCreate()
        windowManipulator.showViews()
        //SettingsManager.getInstance(this).addListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManipulator.removeViews()
        // SettingsManager.getInstance(this).removeListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
