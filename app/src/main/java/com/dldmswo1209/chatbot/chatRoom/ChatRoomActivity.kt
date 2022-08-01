package com.dldmswo1209.chatbot.chatRoom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.ActivityChatRoomBinding

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatRoomBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttonClickEvent()
    }
    private fun buttonClickEvent(){
        binding.chatRoomLeftArrowButton.setOnClickListener {
            finish()
        }
    }
}