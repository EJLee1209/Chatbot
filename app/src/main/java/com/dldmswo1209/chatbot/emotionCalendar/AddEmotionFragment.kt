package com.dldmswo1209.chatbot.emotionCalendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentAddEmotionBinding

class AddEmotionFragment : Fragment(R.layout.fragment_add_emotion) {
    private lateinit var binding: FragmentAddEmotionBinding
    private lateinit var calendarFragment: CalendarFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddEmotionBinding.bind(view)
        calendarFragment = CalendarFragment()

        binding.backButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(calendarFragment)
        }
        binding.checkButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(calendarFragment)
        }
    }
}