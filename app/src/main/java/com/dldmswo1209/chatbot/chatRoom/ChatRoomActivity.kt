package com.dldmswo1209.chatbot.chatRoom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.ActivityChatRoomBinding

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatRoomBinding
    private val chatList = mutableListOf<ChatItem>(
        ChatItem("오늘은 무슨 일이 있었니?", TYPE_BOT)
    )
    private val chatAdapter = ChatListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val intent = intent
        if(intent != null){
            // 홈 화면에서 채팅을 입력해서 채팅방으로 들어온 경우
            val chat = intent.getStringExtra("chat")
            val newChat = chat?.let { ChatItem(it, TYPE_USER) }
            if (newChat != null) {
                chatList.add(newChat)
            }
        }
        binding.chatRecyclerView.apply {
            chatAdapter.submitList(chatList)
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(context)
        }

        buttonClickEvent()
    }
    private fun buttonClickEvent(){
        binding.chatRoomLeftArrowButton.setOnClickListener {
            finish()
        }
        binding.inputTextSendButton.setOnClickListener {
            val newText = binding.inputEditTextView.text.toString()
            if(newText == "") return@setOnClickListener
            val newChat = ChatItem(newText, TYPE_USER)
            chatList.add(newChat)
            chatAdapter.submitList(chatList)
            chatAdapter.notifyDataSetChanged()
            binding.chatRecyclerView.scrollToPosition(chatList.size-1)
            binding.inputEditTextView.text.clear()
            // todo AI 모델 적용 후 메시지를 인식해서 챗봇 메시지를 추가하는 기능
        }
    }
}