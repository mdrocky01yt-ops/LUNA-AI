package com.luna.ai.core.device

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.BatteryManager
import android.provider.Settings
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var isFlashOn = false

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

    fun getBatteryLevel(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    fun changeVolume(increase: Boolean) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val direction = if (increase) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI)
        showToast(if (increase) "Volume Up" else "Volume Down")
    }

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

    // নতুন: ব্রাইটনেস কন্ট্রোল
    fun changeBrightness(increase: Boolean) {
        try {
            val currentBrightness = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS
            )
            // 0 থেকে 255 এর মধ্যে ব্রাইটনেস অ্যাডজাস্ট করা
            val newBrightness = if (increase) {
                (currentBrightness + 50).coerceAtMost(255)
            } else {
                (currentBrightness - 50).coerceAtLeast(10)
            }
            
            Settings.System.putInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                newBrightness
            )
            showToast(if (increase) "Brightness Increased" else "Brightness Decreased")
        } catch (e: Exception) {
            showToast("Cannot change brightness automatically. Needs WRITE_SETTINGS permission.")
        }
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
