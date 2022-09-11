package com.dldmswo1209.chatbot

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.databinding.ActivityMainBinding
import com.dldmswo1209.chatbot.depressionTest.DepressionTestFragment
import com.dldmswo1209.chatbot.depressionTest.HelpFragment
import com.dldmswo1209.chatbot.depressionTest.RecommendTestFragment
import com.dldmswo1209.chatbot.emotionCalendar.AddEmotionFragment
import com.dldmswo1209.chatbot.emotionCalendar.AnalysisEmotionFragment
import com.dldmswo1209.chatbot.emotionCalendar.EmotionCalendar
import com.dldmswo1209.chatbot.home.HomeFragment
import com.dldmswo1209.chatbot.todayTodo.AddTodoFragment
import com.dldmswo1209.chatbot.todayTodo.TodoFragment
import com.dldmswo1209.chatbot.todayTodo.TodoItem
import java.time.LocalDate

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    lateinit var homeFragment : HomeFragment
    val calendarFragment = EmotionCalendar()
    lateinit var todoFragment: TodoFragment
    lateinit var depressionTestFragment: DepressionTestFragment
    lateinit var helpFragment: HelpFragment
    lateinit var recommendTestFragment: RecommendTestFragment
    lateinit var addEmotionFragment: AddEmotionFragment
    lateinit var analysisEmotionFragment: AnalysisEmotionFragment
    lateinit var addTodoFragment: AddTodoFragment
    lateinit var userName : String

    var date = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        userName = sharedPreferences.getString("name",null).toString()

        depressionTestFragment = DepressionTestFragment()
        helpFragment = HelpFragment()
        recommendTestFragment = RecommendTestFragment()
        addEmotionFragment = AddEmotionFragment()
        analysisEmotionFragment = AnalysisEmotionFragment()
        addTodoFragment = AddTodoFragment()
        todoFragment = TodoFragment()
        homeFragment = HomeFragment()

        replaceFragment(homeFragment)

        buttonClickEvent()

        Log.d("testt", LocalDate.now().toString())

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
    fun toast(text: String){
        val layoutInflater = LayoutInflater.from(this).inflate(R.layout.view_holder_toast,null)
        val textView = layoutInflater.findViewById<TextView>(R.id.textViewToast)
        textView.text = text

        val toast = Toast(this)
        toast.view = layoutInflater
        toast.show()
    }

    override fun onResume() {
        super.onResume()
    }

}