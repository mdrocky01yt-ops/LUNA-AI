package com.luna.ai.feature.apikeys

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luna.ai.design.theme.CyanGlow
import com.luna.ai.design.theme.DeepBlack
import com.luna.ai.design.theme.ElectricPurple
import com.luna.ai.design.theme.PremiumWhite

@Composable
fun ApiKeysScreen(
    onBackClick: () -> Unit = {},
    viewModel: ApiKeyViewModel = hiltViewModel()
) {
    val deepSeekKeys by viewModel.deepSeekKeys.collectAsState()
    val geminiKeys by viewModel.geminiKeys.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(top = 24.dp)
    ) {
        // টপ বার
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PremiumWhite)
            }
            Text(text = "API Keys", color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ApiKeySection(
                    title = "DeepSeek API Keys",
                    keys = deepSeekKeys,
                    onAdd = { viewModel.addDeepSeekKey(it) },
                    onRemove = { viewModel.removeDeepSeekKey(it) }
                )
            }
            item {
                ApiKeySection(
                    title = "Gemini API Keys",
                    keys = geminiKeys,
                    onAdd = { viewModel.addGeminiKey(it) },
                    onRemove = { viewModel.removeGeminiKey(it) }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun ApiKeySection(
    title: String,
    keys: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    var newKey by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF151520), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(text = title, color = CyanGlow, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        // Key ইনপুট বক্স
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newKey,
                onValueChange = { newKey = it },
                placeholder = { Text("Paste API Key here...", color = Color.Gray, fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricPurple,
                    unfocusedBorderColor = Color.DarkGray,
                    focusedTextColor = PremiumWhite,
                    unfocusedTextColor = PremiumWhite,
                    focusedContainerColor = Color(0xFF0A0A10),
                    unfocusedContainerColor = Color(0xFF0A0A10)
                )
            )
            Spacer(modifier = Modifier.size(8.dp))
            IconButton(
                onClick = {
                    onAdd(newKey)
                    newKey = ""
                },
                modifier = Modifier.size(45.dp).background(ElectricPurple, CircleShape)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Key", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // যোগ করা Key-গুলোর লিস্ট
        if (keys.isEmpty()) {
            Text(text = "No keys added yet.", color = Color.Gray, fontSize = 12.sp)
        } else {
            keys.forEachIndexed { index, key ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color(0xFF0A0A10), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Key ${index + 1}: ${if (key.length > 10) key.take(8) + "..." + key.takeLast(4) else "***"}",
                        color = PremiumWhite,
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp).clickable { onRemove(key) }
                    )
                }
            }
        }
    }
}
