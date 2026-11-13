package com.luna.ai.core.device

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.BatteryManager
import android.widget.Toast
import androidx.core.content.ContextCompat

class DeviceController(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var isFlashOn = false

    // ১. টর্চ অন/অফ করা
    fun toggleFlashlight() {
        try {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, !isFlashOn)
            isFlashOn = !isFlashOn
            showToast(if (isFlashOn) "Flashlight ON" else "Flashlight OFF")
        } catch (e: Exception) {
            showToast("Flashlight not available")
        }
    }

    // ২. ব্যাটারি পার্সেন্টেজ
    fun getBatteryLevel(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    // ৩. ভলিউম বাড়ানো/কমানো
    fun changeVolume(increase: Boolean) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val direction = if (increase) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI)
        showToast(if (increase) "Volume Up" else "Volume Down")
    }

    // ৪. অ্যাপ ওপেন করা
    fun openApp(packageName: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        return if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } else {
            showToast("App not found on this phone.")
            false
        }
    }

    // ৫. ইনস্টল করা অ্যাপের লিস্ট বের করা
    fun getInstalledApps(): List<String> {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(0)
        return apps.filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .map { it.packageName }
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
