
package com.luna.ai.service

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.luna.ai.MainActivity
import com.luna.ai.ai.model.ChatMessage
import com.luna.ai.ai.repository.AiRepository
import com.luna.ai.core.database.ChatDao
import com.luna.ai.core.datastore.DataStoreManager
import com.luna.ai.core.device.ActionParser
import com.luna.ai.core.model.Message
import com.luna.ai.core.utils.PromptHelper
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FloatingBubbleService : LifecycleService() {

    @Inject
    lateinit var repository: AiRepository

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var chatDao: ChatDao

    @Inject
    lateinit var actionParser: ActionParser

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: ComposeView
    private lateinit var params: WindowManager.LayoutParams

    override fun onCreate() {
        super.onCreate()

        windowManager =
            getSystemService(WINDOW_SERVICE) as WindowManager

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
                var showMiniChat by remember { mutableStateOf(false) }

                Box(contentAlignment = Alignment.Center) {
                    if (showMiniChat) {
                        MiniChatView(
                            onClose = {
                                showMiniChat = false
                            },
                            onSendMessage = { text, onResult ->
                                lifecycleScope.launch {
                                    try {
                                        chatDao.insertMessage(
                                            Message(
                                                text = text,
                                                isFromUser = true
                                            )
                                        )

                                        val personality =
                                            dataStoreManager.aiPersonalityFlow.first()

                                        val history =
                                            mutableListOf<ChatMessage>()

                                        history.add(
                                            ChatMessage(
                                                role = "system",
                                                content = PromptHelper.getSystemPrompt(
                                                    personality
                                                )
                                            )
                                        )

                                        val savedMessages =
                                            chatDao.getAllMessages().first()

                                        savedMessages
                                            .takeLast(40)
                                            .filterNot { message ->
                                                !message.isFromUser &&
                                                    message.text.startsWith("Error:")
                                            }
                                            .forEach { message ->
                                                history.add(
                                                    ChatMessage(
                                                        role = if (message.isFromUser) {
                                                            "user"
                                                        } else {
                                                            "assistant"
                                                        },
                                                        content = message.text
                                                    )
                                                )
                                            }

                                        val result =
                                            repository.sendMessage(history)

                                        result.onSuccess { reply ->
                                            val finalReply =
                                                if (reply.contains("ACTION:")) {
                                                    actionParser.executeAction(reply)
                                                        ?: "দুঃখিত, কাজটি সম্পন্ন করা যায়নি।"
                                                } else {
                                                    reply
                                                }

                                            chatDao.insertMessage(
                                                Message(
                                                    text = finalReply,
                                                    isFromUser = false
                                                )
                                            )

                                            onResult(finalReply)
                                        }.onFailure { error ->
                                            onResult(
                                                "সমস্যা হয়েছে: ${error.message ?: "অজানা সমস্যা"}"
                                            )
                                        }
                                    } catch (error: Exception) {
                                        onResult(
                                            "সমস্যা হয়েছে: ${error.message ?: "অজানা সমস্যা"}"
                                        )
                                    }
                                }
                            },
                            onReadScreen = { onResult ->
                                val screenText =
                                    LunaAccessibilityService.instance
                                        ?.getScreenText()
                                        ?: "অ্যাক্সেসিবিলিটি পারমিশন দেওয়া নেই।"

                                onResult(
                                    "স্ক্রিনে লেখা আছে:\n$screenText"
                                )
                            }
                        )
                    } else if (isExpanded) {
                        Row(
                            modifier = Modifier
                                .background(
                                    Color(0xEE1A1A2E),
                                    RoundedCornerShape(30.dp)
                                )
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                            horizontalArrangement =
                                Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isMuted) {
                                    Icons.Default.MicOff
                                } else {
                                    Icons.Default.Mic
                                },
                                contentDescription = "Mute",
                                tint = if (isMuted) {
                                    Color.Red
                                } else {
                                    CyanGlow
                                },
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable {
                                        isMuted = !isMuted
                                    }
                            )

                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Chat",
                                tint = CyanGlow,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable {
                                        showMiniChat = true
                                    }
                            )

                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Open App",
                                tint = PremiumWhite,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable {
                                        val intent = Intent(
                                            this@FloatingBubbleService,
                                            MainActivity::class.java
                                        ).apply {
                                            addFlags(
                                                Intent.FLAG_ACTIVITY_NEW_TASK
                                            )
                                        }

                                        startActivity(intent)
                                        floatingView.visibility = View.GONE
                                    }
                            )

                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.Gray,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clickable {
                                        isExpanded = false
                                    }
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            ElectricPurple,
                                            CyanGlow
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .clickable {
                                    isExpanded = true
                                },
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

            setOnTouchListener(object : View.OnTouchListener {

                private var initialX = 0
                private var initialY = 0
                private var initialTouchX = 0f
                private var initialTouchY = 0f

                override fun onTouch(
                    view: View,
                    event: MotionEvent
                ): Boolean {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                            return false
                        }

                        MotionEvent.ACTION_MOVE -> {
                            params.x =
                                initialX +
                                    (initialTouchX - event.rawX).toInt()

                            params.y =
                                initialY +
                                    (initialTouchY - event.rawY).toInt()

                            if (floatingView.isAttachedToWindow) {
                                windowManager.updateViewLayout(
                                    floatingView,
                                    params
                                )
                            }

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
        if (::floatingView.isInitialized &&
            floatingView.isAttachedToWindow
        ) {
            windowManager.removeView(floatingView)
        }

        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}

@Composable
fun MiniChatView(
    onClose: () -> Unit,
    onSendMessage: (String, (String) -> Unit) -> Unit,
    onReadScreen: ((String) -> Unit) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    var responseText by remember {
        mutableStateOf(
            "আমি এখানে আছি। কিছু জিজ্ঞেস করুন বা স্ক্রিন পড়তে বলুন।"
        )
    }

    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .width(280.dp)
            .background(
                Color(0xEE1A1A2E),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Luna Mini-Chat",
                color = CyanGlow,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.Gray,
                modifier = Modifier
                    .size(20.dp)
                    .clickable {
                        onClose()
                    }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(
                    Color(0xFF0A0A10),
                    RoundedCornerShape(8.dp)
                )
                .clickable {
                    isLoading = true

                    onReadScreen { result ->
                        responseText = result
                        isLoading = false
                    }
                }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Visibility,
                contentDescription = "Read Screen",
                tint = PremiumWhite,
                modifier = Modifier.size(16.dp)
            )

            Text(
                " স্ক্রিন পড়ে বুঝিয়ে দাও",
                color = PremiumWhite,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            text = responseText,
            color = Color.LightGray,
            fontSize = 13.sp,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .height(80.dp)
        )

        if (isLoading) {
            CircularProgressIndicator(
                color = CyanGlow,
                modifier = Modifier.size(20.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = {
                    inputText = it
                },
                placeholder = {
                    Text(
                        "Ask...",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF0A0A10),
                    unfocusedContainerColor = Color(0xFF0A0A10),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send",
                tint = CyanGlow,
                modifier = Modifier
                    .size(28.dp)
                    .padding(start = 8.dp)
                    .clickable {
                        if (inputText.isNotBlank() && !isLoading) {
                            isLoading = true

                            val textToSend = inputText.trim()
                            inputText = ""

                            onSendMessage(textToSend) { result ->
                                responseText = result
                                isLoading = false
                            }
                        }
                    }
            )
        }
    }
}
