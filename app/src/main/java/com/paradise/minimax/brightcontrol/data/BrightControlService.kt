package com.paradise.minimax.brightcontrol.data

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import com.paradise.minimax.brightcontrol.view.WindowManipulator


class BrightControlService : Service() {

    companion object {
        var isRunning = false
    }

    private val windowManipulator by lazy { WindowManipulator(this) }

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        val notification = getNotification(this)
        startForeground(666, notification)
        windowManipulator.showViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManipulator.removeViews()
        isRunning = false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun getNotification(context: Context): Notification {
        val builder = NotificationCompat.Builder(context, "")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
            builder.priority = Notification.PRIORITY_MIN
        else
            builder.priority = NotificationManager.IMPORTANCE_NONE
        return builder.build()
    }
}
