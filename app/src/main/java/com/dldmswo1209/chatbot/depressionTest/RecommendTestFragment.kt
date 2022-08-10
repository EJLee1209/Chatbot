package com.dldmswo1209.chatbot.depressionTest

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentRecommendTestBinding

class RecommendTestFragment : Fragment(R.layout.fragment_recommend_test) {
    private lateinit var binding: FragmentRecommendTestBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecommendTestBinding.bind(view)

        binding.getTestButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).depressionTestFragment)
        }
        binding.backButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).calendarFragment)
        }
    }
}