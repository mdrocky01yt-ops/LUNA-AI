package com.luna.ai.feature.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luna.ai.core.model.Message
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.feature.chat.components.ChatBubble
import com.luna.ai.feature.chat.components.ChatInput

@Composable
fun ChatScreen(
    onBackClick: () -> Unit = {}
) {
    val messages = remember { mutableStateListOf<Message>() }

    // ডেমো মেসেজ (আপাতত)
    if (messages.isEmpty()) {
        messages.add(Message("1", "হ্যালো! আমি লুনা। আজ আমি আপনাকে কীভাবে সাহায্য করতে পারি?", false))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // টপ বার
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A0A10))
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    
                    // লুনা এআই-এর প্রোফাইল ছবি (ডেমো)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Brush.linearGradient(listOf(Color(0xFF8B5CF6), Color(0xFF22D3EE))), CircleShape)
                    )
                    
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(text = "Luna AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = "Online", color = Color(0xFF10B981), fontSize = 12.sp)
                    }
                }
                
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
                }
            }

            // চ্যাট লিস্ট
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(message = message)
                }
            }

            // ইনপুট এরিয়া
            ChatInput(
                onSendMessage = { text ->
                    // ইউজারের মেসেজ যোগ করা
                    messages.add(Message(System.currentTimeMillis().toString(), text, true))
                    
                    // ডেমো রিপ্লাই (এখানে পরে AI ইঞ্জিন যুক্ত হবে)
                    messages.add(Message(System.currentTimeMillis().toString() + "1", "আমি বুঝতে পেরেছি। আমি এখনো ডেভেলপমেন্ট মোডে আছি, শীঘ্রই আমি সত্যিকারের উত্তর দিতে পারব!", false))
                }
            )
        }
    }
}
