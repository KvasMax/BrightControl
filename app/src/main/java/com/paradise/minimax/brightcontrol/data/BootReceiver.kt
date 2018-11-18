package com.paradise.minimax.brightcontrol.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (SettingsManager.getInstance(context).startOnBoot) {
            context.startService(Intent(context, BrightControlService::class.java))
        }
    }
}
