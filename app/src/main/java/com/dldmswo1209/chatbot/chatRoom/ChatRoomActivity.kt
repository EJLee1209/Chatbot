package com.dldmswo1209.chatbot.chatRoom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.ActivityChatRoomBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime

/***
 * 채팅 DB 구조
 * FireBase Realtime Database
 * Chat -> 현재날짜(2022-08-11) -> 현재시간(12:55:10) -> ChatItem() 객체로 저장
 * 날짜별로 채팅 아이템을 저장해서 채팅방을 들어갔을 때 오늘 챗봇과 나눈 대화 목록만 사용자에게 보여줌
 */

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatRoomBinding
    private val chatList = mutableListOf<ChatItem>(
        ChatItem("오늘은 무슨 일이 있었니?", TYPE_BOT)
    )
    private val chatAdapter = ChatListAdapter()
    private val listener = object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val chatItem = snapshot.getValue(ChatItem::class.java)
            chatItem ?: return

            chatList.add(chatItem)
            chatAdapter.submitList(chatList)
            chatAdapter.notifyDataSetChanged()
            binding.chatRecyclerView.scrollToPosition(chatList.size-1)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatList.clear()
        chatList.add(ChatItem("오늘은 무슨 일이 있었니?", TYPE_BOT))

        val intent = intent
        if(intent != null){
            // 홈 화면에서 채팅을 입력해서 채팅방으로 들어온 경우
            val chat = intent.getStringExtra("chat")
            val newChat = chat?.let { ChatItem(it, TYPE_USER) }
            if (newChat != null) {
                chatItemPushToDB(newChat)
            }
        }
        binding.chatRecyclerView.apply {
            chatAdapter.submitList(chatList)
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(context)
        }

        val chatDB = Firebase.database.reference.child("Chat").child(LocalDate.now().toString())
        chatDB.addChildEventListener(listener)

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

            chatItemPushToDB(newChat)
            binding.inputEditTextView.text.clear()
            // todo AI 모델 적용 후 메시지를 인식해서 챗봇 메시지를 추가하는 기능
        }
    }
    private fun chatItemPushToDB(chat: ChatItem){ // ChatItem 을 firebase RealtimeDB 에 저장하는 메소드
        // DB 경로 이름에 특수기호 . # $ [ ] 는 포함 되면 안되서 .을 제거하기 위함
        var localDateTime = LocalDateTime.now().toString()
        var currentDate = localDateTime.split(".")[0]
        var currentTime = currentDate.split("T")[1]
        // DB 구조
        // Chat -> 현재날짜+시간(2022-08-11T00:14:55) -> ChatItem() 객체로 저장
        val chatDB = Firebase.database.reference.child("Chat").child(LocalDate.now().toString())
        chatDB
            .child(currentTime)
            .setValue(chat)
        chatAdapter.notifyDataSetChanged()
        // 임의로 딜레이를 줘서 AI와 대화하는 것 처럼 보이기 위함
        Handler(Looper.getMainLooper()).postDelayed({
            //실행할 코드
            val botChat = ChatItem("ㅋㅋㅋㅋ", TYPE_BOT)// 테스트 채팅(AI)
            localDateTime = LocalDateTime.now().toString()
            currentDate = localDateTime.split(".")[0]
            currentTime = currentDate.split("T")[1]
            chatDB
                .child(currentTime)
                .setValue(botChat)

            chatAdapter.notifyDataSetChanged()
        }, 1000)


    }
}