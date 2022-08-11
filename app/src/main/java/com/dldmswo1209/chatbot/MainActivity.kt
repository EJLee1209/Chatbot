package com.dldmswo1209.chatbot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.dldmswo1209.chatbot.databinding.ActivityMainBinding
import com.dldmswo1209.chatbot.depressionTest.DepressionTestFragment
import com.dldmswo1209.chatbot.depressionTest.HelpFragment
import com.dldmswo1209.chatbot.depressionTest.RecommendTestFragment
import com.dldmswo1209.chatbot.emotionCalendar.AddEmotionFragment
import com.dldmswo1209.chatbot.emotionCalendar.AnalysisEmotionFragment
import com.dldmswo1209.chatbot.emotionCalendar.CalendarFragment
import com.dldmswo1209.chatbot.home.HomeFragment
import com.dldmswo1209.chatbot.todayTodo.TodoFragment
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val todoFragment = TodoFragment()
    private val homeFragment = HomeFragment()
    val calendarFragment = CalendarFragment()
    lateinit var depressionTestFragment: DepressionTestFragment
    lateinit var helpFragment: HelpFragment
    lateinit var recommendTestFragment: RecommendTestFragment
    lateinit var addEmotionFragment: AddEmotionFragment
    lateinit var analysisEmotionFragment: AnalysisEmotionFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        depressionTestFragment = DepressionTestFragment()
        helpFragment = HelpFragment()
        recommendTestFragment = RecommendTestFragment()
        addEmotionFragment = AddEmotionFragment()
        analysisEmotionFragment = AnalysisEmotionFragment()

        replaceFragment(homeFragment)

        buttonClickEvent()

        val intent = intent
        val yes = intent.getBooleanExtra("yes",false)
        if(yes){
            binding.calendarButton.performClick() // 캘린더 프래그먼트로 이동 하기 위해서 강제 클릭 이벤트 발생 시키기
        }

    }
    private fun buttonClickEvent(){
        binding.HomeButton.setOnClickListener {
            replaceFragment(homeFragment)
            binding.selectRectangleHome.isVisible = true
            binding.selectRectangleTodo.isVisible = false
            binding.selectRectangleCalendar.isVisible = false
        }
        binding.todoButton.setOnClickListener {
            replaceFragment(todoFragment)
            binding.selectRectangleHome.isVisible = false
            binding.selectRectangleTodo.isVisible = true
            binding.selectRectangleCalendar.isVisible = false
        }
        binding.calendarButton.setOnClickListener {
            replaceFragment(calendarFragment)
            binding.selectRectangleHome.isVisible = false
            binding.selectRectangleTodo.isVisible = false
            binding.selectRectangleCalendar.isVisible = true
        }
    }
   fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainerView, fragment)
                commit()
            }
    }

}