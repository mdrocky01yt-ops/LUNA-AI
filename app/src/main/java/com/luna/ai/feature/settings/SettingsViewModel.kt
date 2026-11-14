package com.luna.ai.feature.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    // এই ফাইলে এখন আর কোনো DeepSeek বা API Key-এর লজিক নেই।
    // সেটিংস স্ক্রিনে শুধু পারমিশন ও তথ্য দেখানো হয়।
}
