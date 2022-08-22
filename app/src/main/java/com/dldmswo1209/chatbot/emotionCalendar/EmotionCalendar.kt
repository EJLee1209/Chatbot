package com.dldmswo1209.chatbot.emotionCalendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.dldmswo1209.chatbot.MainActivity

import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.adapter.CalendarFragmentAdapter
import com.dldmswo1209.chatbot.databinding.EmotionCalendarBinding
import com.dldmswo1209.chatbot.databinding.FragmentCalendarBinding
import kotlinx.android.synthetic.main.emotion_calendar.*
import java.util.*

class EmotionCalendar : Fragment(R.layout.emotion_calendar) {
    private lateinit var binding: EmotionCalendarBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EmotionCalendarBinding.bind(view)

        val calendarFragmentAdapter = CalendarFragmentAdapter(requireActivity())

        binding.emotionCalendar.apply {
            adapter = calendarFragmentAdapter
            orientation = ViewPager2.ORIENTATION_VERTICAL
            setCurrentItem(calendarFragmentAdapter.firstFragmentPosition, false)
        }


        binding.addEmotionButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).addEmotionFragment)
        }
        binding.calendarFrameLayout.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).analysisEmotionFragment)
        }

    }

}