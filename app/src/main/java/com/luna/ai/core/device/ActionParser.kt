package com.luna.ai.core.device

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionParser @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val deviceController = DeviceController(context)

    fun executeAction(aiResponse: String): Boolean {
        // AI যদি এই ফরম্যাটে উত্তর দেয়: "ACTION:OPEN_APP:com.whatsapp"
        if (aiResponse.contains("ACTION:OPEN_APP:")) {
            val appPackage = aiResponse.substringAfter("ACTION:OPEN_APP:").substringBefore(" ").trim()
            return deviceController.openApp(appPackage)
        }

        if (aiResponse.contains("ACTION:FLASHLIGHT")) {
            deviceController.toggleFlashlight()
            return true
        }

        if (aiResponse.contains("ACTION:VOLUME_UP")) {
            deviceController.changeVolume(true)
            return true
        }

        if (aiResponse.contains("ACTION:VOLUME_DOWN")) {
            deviceController.changeVolume(false)
            return true
        }

        if (aiResponse.contains("ACTION:BATTERY")) {
            deviceController.showToast("Battery: ${deviceController.getBatteryLevel()}%")
            return true
        }

        return false
    }
}
