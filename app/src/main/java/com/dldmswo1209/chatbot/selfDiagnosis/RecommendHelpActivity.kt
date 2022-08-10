package com.dldmswo1209.chatbot.selfDiagnosis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.ActivityRecommendHelpBinding

class RecommendHelpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecommendHelpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendHelpBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_recommend_help)

        binding.requestHelpButton.setOnClickListener {
            // 도움 요청
            finish()
        }
        binding.backButton.setOnClickListener {
            finish()
        }

    }
}