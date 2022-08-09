package com.dldmswo1209.chatbot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dldmswo1209.chatbot.chatRoom.ChatRoomActivity
import com.dldmswo1209.chatbot.todayTodo.TodoFragment
import kotlinx.android.synthetic.main.activity_emotioncal.*
import kotlinx.android.synthetic.main.activity_main.*

class emotioncal : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emotioncal)

        todbutton.setOnClickListener{
            val intent = Intent(this, TodoFragment::class.java)
            startActivity(intent)
        }
        chatbtn.setOnClickListener{
            val intent_ch = Intent(this, ChatRoomActivity::class.java)
            startActivity(intent_ch)
        }
        emobtn.setOnClickListener{
            val intent_emo = Intent(this,emtional::class.java)
            startActivity(intent_emo)
        }
    }
}