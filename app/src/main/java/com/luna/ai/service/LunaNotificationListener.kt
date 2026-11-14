package com.luna.ai.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class LunaNotificationListener : NotificationListenerService() {

    companion object {
        var latestNotificationText: String = "No recent notifications."
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        val extras = sbn.notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: "Unknown"
        val text = extras.getString(Notification.EXTRA_TEXT) ?: "No content"

        latestNotificationText = "App: ${sbn.packageName}, Title: $title, Text: $text"
        Log.d("LunaNotification", latestNotificationText)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
