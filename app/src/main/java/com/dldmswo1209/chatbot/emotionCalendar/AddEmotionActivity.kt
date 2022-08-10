package com.dldmswo1209.chatbot.emotionCalendar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.ActivityAddEmotionBinding

class AddEmotionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddEmotionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEmotionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}