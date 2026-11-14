package com.luna.ai.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleService
import com.luna.ai.MainActivity
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite // এই লাইনটি আগে মিসিং ছিল

class FloatingBubbleService : LifecycleService() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ComposeView
    private lateinit var params: WindowManager.LayoutParams

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createFloatingBubble()
    }

    private fun createFloatingBubble() {
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.END
            x = 40
            y = 200
        }

        floatingView = ComposeView(this).apply {
            setContent {
                var isExpanded by remember { mutableStateOf(false) }
                var isMuted by remember { mutableStateOf(false) }

                Box(contentAlignment = Alignment.Center) {
                    if (isExpanded) {
                        // মিনি মেনু
                        Row(
                            modifier = Modifier
                                .background(Color(0xEE1A1A2E), RoundedCornerShape(30.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // মাইক অন/অফ
                            Icon(
                                imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute",
                                tint = if (isMuted) Color.Red else CyanGlow,
                                modifier = Modifier.size(28.dp).clickable { isMuted = !isMuted }
                            )
                            
                            // মিনি চ্যাট
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Chat",
                                tint = CyanGlow,
                                modifier = Modifier.size(28.dp).clickable {
                                    val intent = Intent(this@FloatingBubbleService, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                }
                            )

                            // অ্যাপ ওপেন
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Open App",
                                tint = PremiumWhite,
                                modifier = Modifier.size(28.dp).clickable {
                                    val intent = Intent(this@FloatingBubbleService, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    floatingView.visibility = View.GONE
                                }
                            )
                        }
                    } else {
                        // মূল বাবল
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(
                                    brush = Brush.linearGradient(listOf(ElectricPurple, CyanGlow)),
                                    shape = CircleShape
                                )
                                .clickable { isExpanded = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Luna",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
            
            // বাবল ড্র্যাগ করার ব্যবস্থা
            setOnTouchListener(object : View.OnTouchListener {
                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return false 
                        }
                        MotionEvent.ACTION_MOVE -> {
                            params.x = initialX + (initialTouchX - event.rawX).toInt()
                            params.y = initialY + (initialTouchY - event.rawY).toInt()
                            windowManager.updateViewLayout(floatingView, params)
                            return true
                        }
                    }
                    return false
                }
            })
        }

        windowManager.addView(floatingView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}
