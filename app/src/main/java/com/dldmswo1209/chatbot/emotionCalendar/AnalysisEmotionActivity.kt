package com.dldmswo1209.chatbot.emotionCalendar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.ActivityAnalysisEmotionBinding
import com.dldmswo1209.chatbot.selfDiagnosis.RecommendSelfDiagnosisActivity

class AnalysisEmotionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAnalysisEmotionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysisEmotionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.confirmButton.setOnClickListener { finish() }
        binding.testButton.setOnClickListener {
            val intent = Intent(this, RecommendSelfDiagnosisActivity::class.java)
            startActivity(intent)
        }
    }
}