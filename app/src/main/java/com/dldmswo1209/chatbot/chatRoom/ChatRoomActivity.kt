package com.dldmswo1209.chatbot.chatRoom

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.ActivityChatRoomBinding
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/***
 * %%%%%%% 필독 %%%%%%%%
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
    private lateinit var chatAdapter : ChatListAdapter
    private val listener = object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val chatItem = snapshot.getValue(ChatItem::class.java) // DB에서 객체 형태로 가져옴
            chatItem ?: return // null 처리

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

        chatAdapter = ChatListAdapter { yesOrNo ->
            if (yesOrNo) {
                // true : 좋아! 를 누른 경우
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("yes", true)
                startActivity(intent)
                finish()
            } else {
                // false : 아니 를 누른 경우
                Toast.makeText(this, "ㅠㅠ",Toast.LENGTH_SHORT).show()
            }
        }

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

        val chatDB = Firebase.database.reference.child(DB_PATH_CHAT).child(LocalDate.now().toString())
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
        var localDateTime = LocalDateTime.now().toString()
        // DB 경로 이름에 특수기호 . # $ [ ] 는 포함 되면 안되서 .을 제거하기 위함
        var currentDate = localDateTime.split(".")[0]
        // 날짜 + 시간에서 시간만 가져옴
        var currentTime = currentDate.split("T")[1]
        // DB 구조
        // Chat -> 현재날짜(2022-08-11) -> 현재시간(12:55:10) -> ChatItem() 객체로 저장
        val chatDB = Firebase.database.reference.child(DB_PATH_CHAT).child(LocalDate.now().toString())
        chatDB
            .child(currentTime)
            .setValue(chat)
        chatAdapter.notifyDataSetChanged()

        // 임의의 봇 채팅 리스트
        // 사용자가 채팅을 보내면 랜덤으로 채팅이 생성됨
        val botChatList = mutableListOf<ChatItem>(
            ChatItem("정말? 즐거운 하루였겠다!", TYPE_BOT),
            ChatItem("그랬구나, 괜찮아 그럴 수 있지", TYPE_BOT),
            ChatItem("힘내!!", TYPE_BOT),
            ChatItem("난 항상 너편이야", TYPE_BOT),
            ChatItem("오늘 있었던 일을 달력에 적어볼까?", TYPE_BOT_RECOMMEND),
            ChatItem("오늘 할 일을 추가해보자!", TYPE_BOT_RECOMMEND),
            ChatItem("오늘의 기분을 추가해볼까?", TYPE_BOT_RECOMMEND),
            ChatItem("너가 진심으로 행복했으면 좋겠다", TYPE_BOT),
            ChatItem("ㅋㅋㅋㅋ", TYPE_BOT),
        )
        val idx = Random().nextInt(botChatList.size)
        // 임의로 딜레이를 줘서 AI와 대화하는 것 처럼 보이기 위함
        // 나중에 이부분을 AI 모델을 활용해서 바꾸면 될듯
        Handler(Looper.getMainLooper()).postDelayed({
            //실행할 코드
            val botChat = botChatList[idx]
            localDateTime = LocalDateTime.now().toString()
            currentDate = localDateTime.split(".")[0]
            currentTime = currentDate.split("T")[1]
            chatDB
                .child(currentTime)
                .setValue(botChat)

            chatAdapter.notifyDataSetChanged()
        }, 1000)


    }
    companion object{
        const val DB_PATH_CHAT = "Chat"
    }
}
