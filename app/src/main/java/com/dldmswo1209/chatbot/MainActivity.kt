package com.dldmswo1209.chatbot

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.databinding.ActivityMainBinding
import com.dldmswo1209.chatbot.depressionTest.DepressionTestFragment
import com.dldmswo1209.chatbot.depressionTest.HelpFragment
import com.dldmswo1209.chatbot.depressionTest.RecommendTestFragment
import com.dldmswo1209.chatbot.emotionCalendar.AddEmotionFragment
import com.dldmswo1209.chatbot.emotionCalendar.AnalysisEmotionFragment
import com.dldmswo1209.chatbot.emotionCalendar.FragmentCalendar
import com.dldmswo1209.chatbot.home.HomeFragment
import com.dldmswo1209.chatbot.todayTodo.AddTodoFragment
import com.dldmswo1209.chatbot.todayTodo.TodoFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val homeFragment = HomeFragment()
    val calendarFragment = FragmentCalendar()
    lateinit var todoFragment: TodoFragment
    lateinit var depressionTestFragment: DepressionTestFragment
    lateinit var helpFragment: HelpFragment
    lateinit var recommendTestFragment: RecommendTestFragment
    lateinit var addEmotionFragment: AddEmotionFragment
    lateinit var analysisEmotionFragment: AnalysisEmotionFragment
    lateinit var addTodoFragment: AddTodoFragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        depressionTestFragment = DepressionTestFragment()
        helpFragment = HelpFragment()
        recommendTestFragment = RecommendTestFragment()
        addEmotionFragment = AddEmotionFragment()
        analysisEmotionFragment = AnalysisEmotionFragment()
        addTodoFragment = AddTodoFragment()
        todoFragment = TodoFragment()

        replaceFragment(homeFragment)

        buttonClickEvent()


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

    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("question", Context.MODE_PRIVATE)
        val yesOrNo = sharedPreferences.getBoolean("yesOrNo", false)

        if(yesOrNo){
            binding.calendarButton.performClick()
            val editor = sharedPreferences.edit()
            editor.apply {
                putBoolean("yesOrNo", false)
                apply()
            }
        }
    }

}