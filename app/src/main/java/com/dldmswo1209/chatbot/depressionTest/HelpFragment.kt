package com.dldmswo1209.chatbot.depressionTest

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentHelpBinding

class HelpFragment : Fragment(R.layout.fragment_help) {
    private lateinit var binding : FragmentHelpBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHelpBinding.bind(view)

        binding.helpMeButton.setOnClickListener {
            // 도움요청
        }
        binding.backButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).calendarFragment)
        }
    }
}