package com.dldmswo1209.chatbot.chatRoom

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.ActivityChatRoomBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import java.net.UnknownHostException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/***
 * %%%%%%% 필독 %%%%%%%%
 * 채팅 DB 구조
 * FireBase Realtime Database
 * Chat -> 현재날짜(2022-08-11) -> 현재시간(12:55:10) -> ChatItem() 객체로 저장
 *
 * 구조를 위와 같이 했을 때의 문제점 : 어플을 사용하는 사람이 2명 이상일 경우 구분이 안됨. 모든 유저의 채팅 목록이 같은 DB 에 저장이 됨
 * 사용자 이름으로 구분하면 해결 되긴 함... 더 좋은 방법은 로그인 기능을 추가해서 user uid(고유한 값) 로 구분하는것
 * 유저 이름으로 구분할 경우, 유저 이름을 입력받는 부분에서 중복체크를 하는 작업을 추가해야 함.
 *
 * ---- DB 구조 수정 ----
 * 이름 -> Chat -> 현재날짜(2022-08-11) -> 현재시간(12:55:10) -> ChatItem() 객체로 저장
 * 날짜별로 채팅 아이템을 저장해서 채팅방을 들어갔을 때 오늘 챗봇과 나눈 대화 목록만 사용자에게 보여줌
 */

class ChatRoomActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatRoomBinding
    private val chatList = mutableListOf<ChatItem>(
        ChatItem("오늘은 무슨 일이 있었니?", TYPE_BOT)
    )
    private lateinit var chatAdapter : ChatListAdapter
    private lateinit var userDB: SharedPreferences
    private lateinit var userName: String
    private lateinit var chatDB: DatabaseReference
    private var client: Socket? = null
    private var dataOutput: DataOutputStream? = null
    private var dataInput: DataInputStream? = null

    private val listener = object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val chatItem = snapshot.getValue(ChatItem::class.java) ?: return // DB 에서 객체 형태로 가져옴


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
        val connect = Connect()
        connect.execute(CONNECT_MSG)

        buttonClickEvent()

        // 채팅정보 초기화 작업
        chatList.clear()
        chatList.add(ChatItem("오늘은 무슨 일이 있었니?", TYPE_BOT))

        // 현재 사용자의 이름을 가져오는 작업
        userDB = getSharedPreferences("user", Context.MODE_PRIVATE)
        userName = userDB?.getString("name",null).toString()

        // 어답터 생성
        chatAdapter = ChatListAdapter { yesOrNo ->
            if (yesOrNo) {
                // true : 좋아! 를 누른 경우
                val sharedPreferences = getSharedPreferences("question", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.apply {
                    putBoolean("yesOrNo", true)
                    apply()
                }
                finish()
            } else {
                // false : 아니 를 누른 경우
                Toast.makeText(this, "ㅠㅠ",Toast.LENGTH_SHORT).show()
            }
        }
        // 리사이클러뷰
        binding.chatRecyclerView.apply {
            chatAdapter.submitList(chatList)
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // DB 에서 데이터를 가져오는 작업
        chatDB = Firebase.database.reference.child(userName).child(DB_PATH_CHAT).child(LocalDate.now().toString())
        chatDB.addChildEventListener(listener)

        // 문제점 : DB 에서 채팅 데이터를 가져오는데 시간이 걸려서 홈 화면에서 채팅을 보낼 경우 이미 존재하던 채팅 내역보다 위에 나오게 된다.
        // 딜레이를 발생시켜서 해결...? 최선인가
        Handler(Looper.getMainLooper()).postDelayed({
            //실행할 코드
            val intent = intent
            if(intent != null){
                // 홈 화면에서 채팅을 입력해서 채팅방으로 들어온 경우
                val chat = intent.getStringExtra("chat")
                val newChat = chat?.let { ChatItem(it, TYPE_USER) }
                if (newChat != null) {
                    chatItemPushToDB(newChat)
                }
            }
        }, 1500)

    }
    private fun buttonClickEvent(){
        binding.chatRoomLeftArrowButton.setOnClickListener {
            finish()
        }
        binding.inputTextSendButton.setOnClickListener {
            val newText = binding.inputEditTextView.text.toString()
            if(newText == "") return@setOnClickListener
//            val newChat = ChatItem(newText, TYPE_USER)
//            chatItemPushToDB(newChat)
            // todo AI 모델 적용 후 메시지를 인식해서 챗봇 메시지를 추가하는 기능
            dataOutput ?: return@setOnClickListener
            Thread {
                dataOutput!!.writeUTF(newText)
            }.start()

        }
    }
    private fun getCurrentTime(): String{
        var localDateTime = LocalDateTime.now().toString()
        // DB 경로 이름에 특수기호 . # $ [ ] 는 포함 되면 안되서 .을 제거하기 위함
        var currentDate = localDateTime.split(".")[0]
        // 날짜 + 시간에서 시간만 가져옴
        var currentTime = currentDate.split("T")[1]

        return currentTime
    }
    private fun chatItemPushToDB(chat: ChatItem){ // ChatItem 을 firebase RealtimeDB 에 저장하는 메소드
        // DB 구조
        // 이름 -> Chat -> 현재날짜(2022-08-11) -> 현재시간(12:55:10) -> ChatItem() 객체로 저장
        chatDB.child(getCurrentTime()).setValue(chat)

        // 임의의 봇 채팅 리스트
        // 사용자가 채팅을 보내면 랜덤으로 채팅이 생성됨
//        val botChatList = mutableListOf<ChatItem>(
//            ChatItem("정말? 즐거운 하루였겠다!", TYPE_BOT),
//            ChatItem("그랬구나, 괜찮아 그럴 수 있지", TYPE_BOT),
//            ChatItem("힘내!!", TYPE_BOT),
//            ChatItem("난 항상 너편이야", TYPE_BOT),
//            ChatItem("오늘 있었던 일을 달력에 적어볼까?", TYPE_BOT_RECOMMEND),
//            ChatItem("오늘 할 일을 추가해보자!", TYPE_BOT_RECOMMEND),
//            ChatItem("오늘의 기분을 추가해볼까?", TYPE_BOT_RECOMMEND),
//            ChatItem("너가 진심으로 행복했으면 좋겠다", TYPE_BOT),
//            ChatItem("ㅋㅋㅋㅋ", TYPE_BOT),
//        )
//        val idx = Random().nextInt(botChatList.size)
        // 임의로 딜레이를 줘서 AI와 대화하는 것 처럼 보이기 위함
        // 나중에 이부분을 AI 모델을 활용해서 바꾸면 될듯
//        Handler(Looper.getMainLooper()).postDelayed({
//            //실행할 코드
//            val botChat = botChatList[idx]
//            chatDB
//                .child(getCurrentTime())
//                .setValue(botChat)
//
//        }, 1000)
    }

    private inner class Connect : AsyncTask<String?, String?, Void?>() {
        private var output_message: String? = null
        private var input_message: String? = null
        override fun doInBackground(vararg strings: String?): Void? {
            try {
                client = Socket(SERVER_IP, 8080)
                dataOutput = DataOutputStream(client!!.getOutputStream())
                dataInput = DataInputStream(client!!.getInputStream())
                output_message = strings[0]
                dataOutput!!.writeUTF(output_message)
                Log.d("testt", output_message!!)

            } catch (e: UnknownHostException) {
                val str = e.message.toString()
                Log.w("discnt", "$str 1")
            } catch (e: IOException) {
                val str = e.message.toString()
                Log.w("discnt", "$str 2")
            }
            while (true) {
                try {
                    val buf = ByteArray(BUF_SIZE)
                    val read_Byte = dataInput!!.read(buf)
                    input_message = String(buf, 0, read_Byte)
                    if (input_message != STOP_MSG) {
                        publishProgress(input_message)
                    } else {
                        // 서버와 연결 끊김
                        val lastChat = ChatItem("오늘 있었던 일을 달력에 적어볼까?", TYPE_BOT_RECOMMEND)
                        chatItemPushToDB(lastChat)
                        client?.close()

                        break
                    }
                    Thread.sleep(2)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        override fun onProgressUpdate(vararg params: String?) {
            val userText = binding.inputEditTextView.text.toString()
            val userChat = ChatItem(userText, TYPE_USER)
            val botChat = ChatItem(params[0].toString(), TYPE_BOT)

            chatItemPushToDB(userChat)

            Handler(Looper.getMainLooper()).postDelayed({
                chatItemPushToDB(botChat)
            }, 1000)

            binding.inputEditTextView.text.clear()
        }


    }

    companion object {
        private const val SERVER_IP = "192.168.219.104"
        private const val CONNECT_MSG = "connect"
        private const val STOP_MSG = "stop"
        private const val BUF_SIZE = 100
        private const val DB_PATH_CHAT = "Chat"
    }

}
