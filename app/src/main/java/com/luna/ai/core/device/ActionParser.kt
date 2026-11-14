package com.luna.ai.core.device

import android.content.Context
import com.luna.ai.service.LunaAccessibilityService
import com.luna.ai.service.LunaNotificationListener
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionParser @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val deviceController = DeviceController(context)

    fun executeAction(aiResponse: String): String? {
        // ১. অ্যাপ ওপেন করা
        if (aiResponse.contains("ACTION:OPEN_APP:")) {
            val appPackage = aiResponse.substringAfter("ACTION:OPEN_APP:").substringBefore(" ").trim()
            deviceController.openApp(appPackage)
            return "অ্যাপ ওপেন করা হয়েছে।"
        }

        // ২. টর্চ
        if (aiResponse.contains("ACTION:FLASHLIGHT")) {
            deviceController.toggleFlashlight()
            return "টর্চ পরিবর্তন করা হয়েছে।"
        }
        
        // ৩. ব্যাটারি
        if (aiResponse.contains("ACTION:BATTERY")) {
            return "আপনার ব্যাটারি: ${deviceController.getBatteryLevel()}%"
        }

        // ৪. ব্রাইটনেস
        if (aiResponse.contains("ACTION:BRIGHTNESS_UP")) {
            deviceController.changeBrightness(true)
            return "ব্রাইটনেস বাড়ানো হয়েছে।"
        }
        if (aiResponse.contains("ACTION:BRIGHTNESS_DOWN")) {
            deviceController.changeBrightness(false)
            return "ব্রাইটনেস কমানো হয়েছে।"
        }

        // ৫. নোটিফিকেশন পড়া
        if (aiResponse.contains("ACTION:READ_NOTIFICATIONS")) {
            return "সর্বশেষ নোটিফিকেশন: ${LunaNotificationListener.latestNotificationText}"
        }

        // ৬. স্ক্রিন রিডিং
        if (aiResponse.contains("ACTION:READ_SCREEN")) {
            val screenText = LunaAccessibilityService.instance?.getScreenText() ?: "অ্যাক্সেসিবিলিটি পারমিশন দেওয়া নেই।"
            return "স্ক্রিনে লেখা আছে:\n$screenText"
        }

        // ৭. টেক্সট ক্লিক
        if (aiResponse.contains("ACTION:CLICK_TEXT:")) {
            val textToClick = aiResponse.substringAfter("ACTION:CLICK_TEXT:").substringBefore("ACTION").trim()
            val success = LunaAccessibilityService.instance?.clickByText(textToClick) ?: false
            return if (success) "$textToClick-তে ক্লিক করা হয়েছে।" else "ক্লিক করা সম্ভব হয়নি।"
        }

        // ৮. স্ক্রল
        if (aiResponse.contains("ACTION:SCROLL_DOWN")) {
            LunaAccessibilityService.instance?.scrollScreen(true)
            return "স্ক্রল ডাউন করা হয়েছে।"
        }
        if (aiResponse.contains("ACTION:SCROLL_UP")) {
            LunaAccessibilityService.instance?.scrollScreen(false)
            return "স্ক্রল আপ করা হয়েছে।"
        }

        // ৯. ব্যাক
        if (aiResponse.contains("ACTION:BACK")) {
            LunaAccessibilityService.instance?.performGlobalBack()
            return "ব্যাক করা হয়েছে।"
        }

        return null // কোনো অ্যাকশন পাওয়া যায়নি
    }
}
