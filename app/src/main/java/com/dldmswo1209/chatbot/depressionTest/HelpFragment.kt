package com.dldmswo1209.chatbot.depressionTest

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentHelpBinding
import kotlinx.android.synthetic.main.activity_main.*

class HelpFragment : Fragment(R.layout.fragment_help) {
    private lateinit var binding : FragmentHelpBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHelpBinding.bind(view)

        binding.helpMeButton.setOnClickListener {
            // 도움요청
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lifeline.or.kr"))
            startActivity(intent)
        }
        binding.backButton.setOnClickListener {
            (activity as MainActivity).HomeButton.performClick()
        }
    }
}