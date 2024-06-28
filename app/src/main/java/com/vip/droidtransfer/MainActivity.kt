package com.vip.droidtransfer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vip.lib_droidtransfer.wifi.WifiP2PUtils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WifiP2PUtils.instance().init(this)
        WifiP2PUtils.instance().registerWifiReceiver(this)
    }

}