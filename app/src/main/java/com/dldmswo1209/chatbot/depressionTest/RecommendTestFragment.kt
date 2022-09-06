package com.dldmswo1209.chatbot.depressionTest

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentRecommendTestBinding
import kotlinx.android.synthetic.main.activity_main.*

class RecommendTestFragment : Fragment(R.layout.fragment_recommend_test) {
    private lateinit var binding: FragmentRecommendTestBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRecommendTestBinding.bind(view)
        val sharedPreferences = (activity as MainActivity).getSharedPreferences("user", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name","사용자명")
        binding.userNameTextView.text = "${name}님"

        binding.getTestButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).depressionTestFragment)
        }
        binding.backButton.setOnClickListener {
            (activity as MainActivity).HomeButton.performClick() // 홈버튼 강제 클릭 -> 홈 화면으로 이동
        }
    }
}