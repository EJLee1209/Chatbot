package com.dldmswo1209.chatbot.depressionTest

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentDepressionTestBinding

class DepressionTestFragment : Fragment(R.layout.fragment_depression_test) {
    private lateinit var binding: FragmentDepressionTestBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDepressionTestBinding.bind(view)

        binding.backButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).recommendTestFragment)
        }
        binding.nextButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).helpFragment)
        }
    }
}