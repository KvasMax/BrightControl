package com.paradise.minimax.brightcontrol.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import com.paradise.minimax.brightcontrol.R
import com.paradise.minimax.brightcontrol.data.BrightControlService
import com.paradise.minimax.brightcontrol.data.SettingsManager
import com.paradise.minimax.brightcontrol.utils.askPermission
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private val settingsManager by lazy { SettingsManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initListeners()

        askPermissions()

    }

    private fun askPermissions() {
        AlertDialog.Builder(this)
                .setMessage("The app needs some permissions to work")
                .setCancelable(false)
                .setPositiveButton("Grant") { _, _ ->
                    askPermission().onAccepted {
                        launchService()
                    }.onDenied {
                        askPermissionsAgain()
                    }.onForeverDenied {
                        it.goToSettings()
                    }
                }
                .create().show()
    }

    private fun askPermissionsAgain() {
        AlertDialog.Builder(this)
                .setMessage("Please, give permissions you refused")
                .setCancelable(false)
                .setPositiveButton("Grant") { _, _ ->
                    askPermission().onAccepted {
                        launchService()
                    }.onDenied {
                        finish()
                    }.onForeverDenied {
                        it.goToSettings()
                    }
                }
                .setNegativeButton("Cancel") { _, _ ->
                    finish()
                }
                .create().show()
    }

    private fun initListeners() {
        transparencySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                settingsManager.setTransparency(progress.toFloat() / seekBar.max)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun permissionIsGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED || checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun sendPermissionRequest(permission: String, newMethod: Boolean, requestCode: Int) {

        if (newMethod) {
            requestPermissions(arrayOf(permission), requestCode)
        } else {
            val intent = Intent(permission)
            startActivityForResult(intent, requestCode)
        }

    }

    private fun launchServiceWithCheck(isChecked: Boolean) {
        if (isChecked) {
            if (checkPermissions()) {
                var areNotificationsEnabled = false
                if (Build.VERSION.SDK_INT > 23) {
                    val notificationManager = NotificationManagerCompat.from(this)
                    areNotificationsEnabled = notificationManager.areNotificationsEnabled()
                }

                /*if (areNotificationsEnabled) {
                    showDisableNotificationDialog(Runnable { this.launchService() })
                } else {
                    launchService()
                }*/

                launchService()
            } else {
            }
        } else {

        }
    }

    private fun launchService() {
        val intent = Intent(applicationContext, BrightControlService::class.java)
        startService(intent)
    }

    private fun checkPermissions(): Boolean {

        var granted = true
        /*if (!permissionIsDenied(Settings.ACTION_MANAGE_WRITE_SETTINGS)) {
            requestPermission(Settings.ACTION_MANAGE_WRITE_SETTINGS, false, "", 1)
            granted = false
        } else*/ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            requestPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, false, "", 1)
            granted = false
        }
        return granted

    }

    private fun requestPermission(permission: String, newMethod: Boolean, message: String, requestCode: Int) {
        if (permissionIsGranted(permission))
            return

        if (newMethod) {
            if (shouldShowRequestPermissionRationale(permission)) {
                showPermissionDialog(permission, newMethod, message, requestCode)

            } else {

                sendPermissionRequest(permission, newMethod, requestCode)
            }
        } else {
            showPermissionDialog(permission, newMethod, message, requestCode)
        }


    }

    private fun showPermissionDialog(permission: String, newMethod: Boolean, message: String, requestCode: Int) {

    }

}
