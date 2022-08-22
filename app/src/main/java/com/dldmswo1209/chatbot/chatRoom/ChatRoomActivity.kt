package com.dldmswo1209.chatbot.chatRoom

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.adapter.ChatListAdapter
import com.dldmswo1209.chatbot.databinding.ActivityChatRoomBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime

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
    lateinit var mRetrofit : Retrofit // 사용할 레트로핏 객체
    lateinit var mRetrofitAPI: RetrofitAPI // 레트로핏 api 객체
    lateinit var mCallAIReply : retrofit2.Call<JsonObject> // Json 형식의 데이터를 요청하는 객체

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


        setRetrofit()
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
                val sharedPreferences = getSharedPreferences("question", MODE_PRIVATE)
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
                    callTodoList(chat)
                }
            }
        }, 1500)

    }
    private fun setRetrofit() {
        // retrofit 으로 가져올 url 을 설정하고 세팅
        mRetrofit = Retrofit.Builder()
            .baseUrl(getString(R.string.baseUrl))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 인터페이스로 만든 레트로핏 api 요청 받는 것 변수로 등록
        mRetrofitAPI = mRetrofit.create(RetrofitAPI::class.java)
    }
    private fun ServerConnectErrorToast(){
        Toast.makeText(this, "서버와의 연결상태가 좋지 않습니다.", Toast.LENGTH_SHORT).show()
    }

    private val mRetrofitCallback = (object : retrofit2.Callback<JsonObject>{
        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
            // 서버에서 데이터 요청 성공시
            val result = response.body()
            Log.d("testt", "결과는 ${result}")

            var gson = Gson()
            val dataParsed1 = gson.fromJson(result, RetrofitDTO.ChatItem::class.java)
            val chatItem = ChatItem(dataParsed1.param, TYPE_BOT)
            chatItemPushToDB(chatItem)

        }

        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            // 서버 요청 실패
            t.printStackTrace()
            Log.d("testt", "에러입니다. ${t.message}")
            ServerConnectErrorToast()
        }
    })
    private fun callTodoList(chatText: String){
        mCallAIReply = mRetrofitAPI.getAIReply(chatText) // RetrofitAPI 에서 JSON 객체를 요청해서 반환하는 메소드 호출
        mCallAIReply.enqueue(mRetrofitCallback) // 응답을 큐에 넣어 대기 시켜놓음. 즉, 응답이 생기면 뱉어낸다.
    }

    private fun buttonClickEvent(){
        binding.chatRoomLeftArrowButton.setOnClickListener {
            finish()
        }
        binding.inputTextSendButton.setOnClickListener {
            // 메세지 보내기 버튼 클릭 이벤트 처리
            var newText = binding.inputEditTextView.text.toString()
            if (newText == "") return@setOnClickListener // 입력이 없으면 아래 코드를 실행하지 않음
            val newChat = ChatItem(newText, TYPE_USER) // 리사이클러뷰에 추가할 ChatItem을 만듬

            if(!newText[newText.length-1].equals(".")){ // 입력문자 마지막에 . 이 없으면 .을 붙임
                newText = "${newText}."
            }
            chatItemPushToDB(newChat) // DB 에 추가 -> DB 리스너를 통해 리사이클러뷰에도 추가 될거임
            // api 서버에 메세지 전달
            callTodoList(newText)
            binding.inputEditTextView.text.clear() // 메세지 입력창 초기화

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
    }


    companion object {
        private const val DB_PATH_CHAT = "Chat"
    }

}
