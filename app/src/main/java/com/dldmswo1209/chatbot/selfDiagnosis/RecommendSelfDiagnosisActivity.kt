package com.dldmswo1209.chatbot.selfDiagnosis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.ActivityRecommendSelfDiagnosisBinding

class RecommendSelfDiagnosisActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRecommendSelfDiagnosisBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecommendSelfDiagnosisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }
        binding.selfDiagnosisButton.setOnClickListener {
            val intent = Intent(this, SelfDiagnosisActivity::class.java)
            startActivity(intent)
        }
    }
}