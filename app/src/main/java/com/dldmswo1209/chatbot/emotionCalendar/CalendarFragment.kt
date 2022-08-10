package com.dldmswo1209.chatbot.emotionCalendar

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.MainActivity

import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment(R.layout.fragment_calendar) {
    private lateinit var binding: FragmentCalendarBinding
    private lateinit var addEmotionFragment: AddEmotionFragment
    private lateinit var analysisEmotionFragment: AnalysisEmotionFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCalendarBinding.bind(view)

        addEmotionFragment = AddEmotionFragment()
        analysisEmotionFragment = AnalysisEmotionFragment()

        binding.addEmotionButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(addEmotionFragment)
        }
        binding.emotionCalendar.setOnClickListener {
            (activity as MainActivity).replaceFragment(analysisEmotionFragment)
        }
    }
}