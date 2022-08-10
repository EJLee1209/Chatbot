package com.dldmswo1209.chatbot.emotionCalendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentAnalysisEmotionBinding

class AnalysisEmotionFragment : Fragment(R.layout.fragment_analysis_emotion) {
    private lateinit var binding: FragmentAnalysisEmotionBinding
    private lateinit var calendarFragment: CalendarFragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAnalysisEmotionBinding.bind(view)
        calendarFragment = CalendarFragment()

        binding.confirmButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(calendarFragment)
        }
    }
}