package com.nocode.netkill

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
iport android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private var isKilled = false
    private lateinit var statusText: TextView
    private lateinit var btnToggle: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(60, 100, 60, 60)
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }
        statusText = TextView(this).apply {
            text = "Internet Status: NORMAL"
            textSize = 20f
            setPadding(0, 0, 0, 60)
        }
        layout.addView(statusText)

        btnToggle = Button(this).apply {
            text = "KILL ALL INTERNET"
            setOnClickListener { toggleInternet() }
        }
        layout.addView(btnToggle)

        val btnDevTiles = Button(this).apply {
            text = "Configure Sensors Off Tile"
            setOnClickListener {
                try {
                    startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "Developer Options not unlocked yet", Toast.LENGTH_SHORT);                }
            }
        }
        layout.addView(btnDevTiles)
        setContentView(layout)
    }

    private fun toggleInternet() {
        if (!isKilled) {
            val vpnIntent = VpnService.prepare(this)
            if (vpnIntent != null) {
                startActivityForResult(vpnIntent, 101)
            } else {
                onActivityResult(101, RESULT_OK, null)
            }
        } else {
            val stopIntent = Intent(this, KillSwitchVpnService::class.java).apply {
                action = "ACTION_DISCONNECT"
            }
            startService(stopIntent)
            isKilled = false
            statusText.text = "Internet Status: NORMAL"
            btnToggle.text = "KML ALL INTERNET"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == RESULT_OK) {
            startService(Intent(this, KillSwitchVpnService::class.java))
            isKilled = true
            statusText.text = "Internet Status: KILLED"
            btnToggle.text = "RESTORE INTERNET"
        }
    }
}
