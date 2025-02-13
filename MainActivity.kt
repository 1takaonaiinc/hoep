package com.example.adguarddnstunnel

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast

class MainActivity : Activity() {

    companion object {
        const val VPN_REQUEST_CODE = 0
        const val EXTRA_DNS_ADDRESS = "com.example.adguarddnstunnel.DNS_ADDRESS"
    }

    private lateinit var dnsInput: EditText
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private var userDns: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }

        dnsInput = EditText(this).apply {
            hint = "Enter DNS Server (e.g., 94.140.14.14)"
        }
        startButton = Button(this).apply {
            text = "Start VPN with DNS"
            setOnClickListener {
                userDns = dnsInput.text.toString().trim()
                if (userDns.isEmpty()) {
                    Toast.makeText(this@MainActivity, "Please enter a DNS server address", Toast.LENGTH_SHORT).show()
                } else {
                    val prepareIntent = VpnService.prepare(this@MainActivity)
                    if (prepareIntent != null) {
                        startActivityForResult(prepareIntent, VPN_REQUEST_CODE)
                    } else {
                        onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null)
                    }
                }
            }
        }
        stopButton = Button(this).apply {
            text = "Stop VPN"
            setOnClickListener {
                val stopIntent = Intent(this@MainActivity, MyVpnService::class.java)
                stopService(stopIntent)
            }
        }

        layout.addView(dnsInput)
        layout.addView(startButton)
        layout.addView(stopButton)
        setContentView(layout)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == VPN_REQUEST_CODE && resultCode == RESULT_OK) {
            val intent = Intent(this, MyVpnService::class.java)
            intent.putExtra(EXTRA_DNS_ADDRESS, userDns)
            startService(intent)
        } else {
            Toast.makeText(this, "VPN permission denied", Toast.LENGTH_SHORT).show()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
