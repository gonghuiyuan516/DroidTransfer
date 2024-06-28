package com.vip.lib_droidtransfer.wifi

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Looper.getMainLooper
import com.blankj.utilcode.util.LogUtils

//WIFI点对点传输工具
class WifiP2PUtils : WifiP2pManager.ChannelListener {

    companion object {
        var mWifiP2pManager: WifiP2pManager? = null
        var wifiChannel: WifiP2pManager.Channel? = null

        private val instance: WifiP2PUtils by lazy { WifiP2PUtils() }

        fun instance(): WifiP2PUtils {
            return instance
        }
    }

    fun init(context: Context) {
        mWifiP2pManager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        wifiChannel = mWifiP2pManager?.initialize(context, getMainLooper(), this)
    }

    fun registerWifiReceiver(activity: Activity) {
        val intentFilter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION) // Wifi 直连可用状态改变
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION) // Wifi 直连发现的设备改变
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION) // Wifi 直连的连接状态改变
        }

        activity.registerReceiver(wifiReceiver, intentFilter)
        //查询附近设备
        mWifiP2pManager?.discoverPeers(wifiChannel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                LogUtils.e("onSuccess ")
            }

            override fun onFailure(p0: Int) {
                LogUtils.e("onFailure ")
            }
        })
    }

    override fun onChannelDisconnected() {
        LogUtils.e("onChannelDisconnected ")
    }

    private val wifiReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {

                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
                        LogUtils.d("Wifi p2p disabled.")
                    } else {
                        LogUtils.d("Wifi p2p enabled.")
                    }
                }

                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    val wifiDevicesList =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra(
                                WifiP2pManager.EXTRA_P2P_DEVICE_LIST, WifiP2pDeviceList::class.java
                            )
                        } else {
                            intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST)
                        }
                    LogUtils.e("WIFI p2p devices: ${wifiDevicesList?.deviceList?.joinToString { "${it.deviceName} -> ${it.deviceAddress}" }}")
                }

                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    LogUtils.e("Connection state change.")
                }
            }
        }
    }
}