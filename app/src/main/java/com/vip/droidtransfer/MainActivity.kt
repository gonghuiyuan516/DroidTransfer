package com.vip.droidtransfer

import android.Manifest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.vip.lib_droidtransfer.wifi.WifiP2PUtils

class MainActivity : AppCompatActivity() {
    private val requestedPermissions = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.NEARBY_WIFI_DEVICES)
        } else {
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }.toTypedArray()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        XXPermissions.with(this)
            // 申请多个权限
            .permission(requestedPermissions)
            // 设置不触发错误检测机制（局部设置）
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                    if (!allGranted) {
                        ToastUtils.showShort("获取部分权限成功，但部分权限未正常授予")
                        return
                    }
                    initWifiP2P()
                    ToastUtils.showShort("获取权限成功")
                }

                override fun onDenied(permissions: MutableList<String>, doNotAskAgain: Boolean) {
                    if (doNotAskAgain) {
                        ToastUtils.showShort("被永久拒绝授权，请手动权限")
                        // 如果是被永久拒绝就跳转到应用权限系统设置页面
                        XXPermissions.startPermissionActivity(this@MainActivity, permissions)
                    } else {
                        ToastUtils.showShort("获取权限失败")
                    }
                }
            })

    }

    fun initWifiP2P() {
        WifiP2PUtils.instance().init(this)
        WifiP2PUtils.instance().registerWifiReceiver(this)
        WifiP2PUtils.instance().createGroup()
        WifiP2PUtils.instance().discoverPeers()
    }

}