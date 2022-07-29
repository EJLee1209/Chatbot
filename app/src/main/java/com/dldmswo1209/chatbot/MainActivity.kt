package com.dldmswo1209.chatbot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
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
        val homeFragment = HomeFragment()
        val todoFragment = TodoFragment()
        val calendarFragment = CalendarFragment()

        replaceFragment(homeFragment)
        binding.bottomNavigationView.selectedItemId = R.id.home
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(homeFragment)
                R.id.todayTodo -> replaceFragment(todoFragment)
                R.id.emotionCalendar -> replaceFragment(calendarFragment)
            }
            true
        }

    }
    private fun replaceFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.fragmentContainerView, fragment)
                commit()
            }
    }
}