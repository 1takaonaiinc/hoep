package com.example.adguarddnstunnel

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor

class MyVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Retrieve the DNS address from the Intent extra; default to AdGuard's DNS if not provided.
        val dnsAddress = intent?.getStringExtra(MainActivity.EXTRA_DNS_ADDRESS) ?: "94.140.14.14"
        establishVpn(dnsAddress)
        return START_STICKY
    }

    private fun establishVpn(dnsAddress: String) {
        if (vpnInterface != null) return

        val builder = Builder()
            .setSession("AdGuard DNS Tunnel")
            .setMtu(1500)
            .addAddress("10.0.0.2", 32)
            .addRoute("0.0.0.0", 0)
            .addDnsServer(dnsAddress)

        vpnInterface = builder.establish()
    }

    override fun onDestroy() {
        vpnInterface?.close()
        vpnInterface = null
        super.onDestroy()
    }
}
