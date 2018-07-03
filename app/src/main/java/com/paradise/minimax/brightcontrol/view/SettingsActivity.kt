package com.paradise.minimax.brightcontrol.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.SeekBar
import com.paradise.minimax.brightcontrol.R
import com.paradise.minimax.brightcontrol.data.BrightControlService
import com.paradise.minimax.brightcontrol.data.SettingsManager
import com.paradise.minimax.brightcontrol.utils.askPermission
import com.paradise.minimax.brightcontrol.utils.shouldAskPermissions
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private val settingsManager by lazy { SettingsManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        initListeners()
        initViews()
        if (shouldAskPermissions()) {
            askPermissions()
        } else {
            launchService()
        }

    }

    private fun initListeners() {
        opacitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                settingsManager.opacity = progress.toFloat() / seekBar.max
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    private fun initViews() {
        opacitySeekBar.progress = (settingsManager.opacity * 100).toInt()
    }

    private fun askPermissions() {
        AlertDialog.Builder(this)
                .setMessage("The app needs some permissions to work")
                .setCancelable(false)
                .setPositiveButton("Grant") { _, _ ->
                    askPermission(acceptedBlock = {
                        launchService()
                    }, deniedBlock = {
                        askPermissionsAgain()
                    })
                }
                .create().show()
    }

    private fun askPermissionsAgain() {
        AlertDialog.Builder(this)
                .setMessage("Please, give permissions you refused")
                .setCancelable(false)
                .setPositiveButton("Grant") { _, _ ->
                    askPermission(acceptedBlock = {
                        launchService()
                    }, deniedBlock = {
                        finish()
                    })
                }
                .setNegativeButton("Cancel") { _, _ ->
                    finish()
                }
                .create().show()
    }

    private fun launchService() {
        val intent = Intent(applicationContext, BrightControlService::class.java)
        startService(intent)
    }


}
