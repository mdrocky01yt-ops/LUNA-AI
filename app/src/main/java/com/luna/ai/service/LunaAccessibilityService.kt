package com.luna.ai.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class LunaAccessibilityService : AccessibilityService() {

    companion object {
        var instance: LunaAccessibilityService? = null
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // আমরা আপাতত স্ক্রিনে যা আছে তা রিড করার ফাংশন বানাবো
    }

    override fun onInterrupt() {
        // ইন্টারাপ্ট হলে কী করবে
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    // ১. পুরো স্ক্রিনের লেখা বের করা
    fun getScreenText(): String {
        val rootNode = rootInActiveWindow ?: return "স্ক্রিনে কিছু পাওয়া যায়নি।"
        val textBuilder = StringBuilder()
        extractText(rootNode, textBuilder)
        return if (textBuilder.isNotEmpty()) textBuilder.toString() else "স্ক্রিনে কোনো লেখা নেই।"
    }

    private fun extractText(node: AccessibilityNodeInfo?, builder: StringBuilder) {
        if (node == null) return
        if (node.text != null) {
            builder.append(node.text).append("\n")
        }
        for (i in 0 until node.childCount) {
            extractText(node.getChild(i), builder)
        }
    }

    // ২. স্ক্রিনে কোনো নির্দিষ্ট লেখা থাকলে সেখানে ক্লিক করা
    fun clickByText(targetText: String): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val nodes = rootNode.findAccessibilityNodeInfosByText(targetText)
        for (node in nodes) {
            if (node.isClickable) {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                return true
            } else {
                // প্যারেন্ট নোড ক্লিকেবল কিনা চেক করা
                var parent = node.parent
                while (parent != null) {
                    if (parent.isClickable) {
                        parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        return true
                    }
                    parent = parent.parent
                }
            }
        }
        return false
    }

    // ৩. স্ক্রল করা
    fun scrollScreen(forward: Boolean): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        val action = if (forward) AccessibilityNodeInfo.ACTION_SCROLL_FORWARD else AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
        val scrollableNode = findScrollableNode(rootNode)
        return scrollableNode?.performAction(action) ?: false
    }

    private fun findScrollableNode(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (node == null) return null
        if (node.isScrollable) return node
        for (i in 0 until node.childCount) {
            val scrollable = findScrollableNode(node.getChild(i))
            if (scrollable != null) return scrollable
        }
        return null
    }

    // ৪. ব্যাক বাটনে ক্লিক করা
    fun performGlobalBack(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_BACK)
    }
    
    // ৫. হোম বাটনে ক্লিক করা
    fun performGlobalHome(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }
}
