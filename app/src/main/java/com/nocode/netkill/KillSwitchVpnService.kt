package com.nocode.netkill

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor

class KillSwitchVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "ACTION_DISCONNECT") {
            stopVpn()
            return START_NOT_STICKY
        }
        try {
            vpnInterface = Builder()
                .setSession("NetKill")
                .addAddress("10.0.0.2", 32)
                .addRoute("0.0.0.0", 0)
                .addRoute("::", 0)
                .addDnsServer("10.0.0.1")
                .setBlocking(true)
                .establish()
        } catch (e: Exception) {
            e.printStackTrace()
            stopSelf()
        }
        return START_STICKY
    }

    private fun stopVpn() {
        try {
            vpnInterface?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        vpnInterface = null
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }
}
