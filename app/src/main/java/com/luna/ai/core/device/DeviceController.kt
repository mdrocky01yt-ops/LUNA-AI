package com.luna.ai.core.device

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.BatteryManager
import android.widget.Toast

class DeviceController(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var isFlashOn = false

    // ১. টর্চ অন/অফ করা
    fun toggleFlashlight() {
        try {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, !isFlashOn)
            isFlashOn = !isFlashOn
            Toast.makeText(context, if (isFlashOn) "Flashlight ON" else "Flashlight OFF", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Flashlight not available", Toast.LENGTH_SHORT).show()
        }
    }

    // ২. ব্যাটারি পার্সেন্টেজ বের করা
    fun getBatteryLevel(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    // ৩. ভলিউম কন্ট্রোল (ভবিষ্যতে ব্যবহারের জন্য)
    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
