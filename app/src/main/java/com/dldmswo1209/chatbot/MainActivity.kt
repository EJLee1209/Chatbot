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
import com.dldmswo1209.chatbot.emotionCalendar.CalendarFragment
import com.dldmswo1209.chatbot.home.HomeFragment
import com.dldmswo1209.chatbot.todayTodo.TodoFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val todoFragment = TodoFragment()
        val homeFragment = HomeFragment()
        val calendarFragment = CalendarFragment()

        replaceFragment(homeFragment)
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
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainerView, fragment)
                commit()
            }
    }
    fun refreshFragment(){
        replaceFragment(TodoFragment())
    }

}