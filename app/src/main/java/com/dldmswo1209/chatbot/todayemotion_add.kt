package com.dldmswo1209.chatbot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_emtional.*

class todayemotion_add : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todayemotion_add)

        teabtn.setOnClickListener {
            val intent = Intent(this,diagnosis2::class.java)
            startActivity(intent)

        }
    }
}