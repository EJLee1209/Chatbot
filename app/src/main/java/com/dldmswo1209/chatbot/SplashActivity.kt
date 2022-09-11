package com.dldmswo1209.chatbot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dldmswo1209.chatbot.databinding.ActivitySplashBinding
import com.dldmswo1209.chatbot.emotionCalendar.EmotionData
import com.dldmswo1209.chatbot.todayTodo.TodoItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.time.LocalDate

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private lateinit var testDB : DatabaseReference
    private val todoList = ArrayList<TodoItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        testDB = Firebase.database.reference.child("sss").child("recommendWorks").child(LocalDate.now().toString())

        CoroutineScope(Dispatchers.IO).launch {
            async {
                testDB.get().addOnCompleteListener {
                    it.result.children.forEach { data ->
                        data.getValue(TodoItem::class.java)?.let { todo ->
                            todoList.add(todo)
                        }
                    }

                }.addOnCanceledListener {

                }


            }
        }

        YoYo.with(Techniques.FadeInDown)
            .duration(3500)
            .playOn(binding.title)

        YoYo.with(Techniques.FadeInDown)
            .duration(3500)
            .playOn(binding.subTitle)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("todoList", todoList)
            startActivity(intent)
        },DURATION)

    }
    companion object{
        private const val DURATION : Long = 2000
    }
}