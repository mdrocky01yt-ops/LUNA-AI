package com.luna.ai.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite

@Composable
fun HomeScreen() {
    // কসমিক ব্যাকগ্রাউন্ড (ডার্ক ব্ল্যাক এবং ডার্ক পার্পল গ্রেডিয়েন্ট)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        DeepBlack,
                        Color(0xFF11001C), // হালকা নিয়ন পার্পল আভা
                        DeepBlack
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ১. টপ বার (লোগো এবং প্রোফাইল)
            TopBar()

            Spacer(modifier = Modifier.weight(1f))

            // ২. এভাটার এবং ভয়েস বাটন এখানে আসবে (পরের ব্যাচে)
            Text(
                text = "AI Avatar Placeholder",
                color = PremiumWhite,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // ৩. বটম নেভিগেশন এখানে আসবে (পরের ব্যাচে)
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // বাম দিকের লোগো এবং নাম
        Column {
            Text(
                text = "🌙 LUNA AI",
                color = PremiumWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Your Smart Companion",
                color = CyanGlow,
                fontSize = 12.sp
            )
        }

        // ডান দিকের ইউজার প্রোফাইল (আপাতত একটি গোলাকার বাটন)
        Box(
            modifier = Modifier
                .size(45.dp)
                .background(ElectricPurple, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "U",
                color = PremiumWhite,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
