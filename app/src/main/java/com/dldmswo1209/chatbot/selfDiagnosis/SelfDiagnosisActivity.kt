package com.dldmswo1209.chatbot.selfDiagnosis

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.ActivitySelfDiagnosisBinding

class SelfDiagnosisActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelfDiagnosisBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelfDiagnosisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.leftArrowButton.setOnClickListener {
            finish()
        }
        binding.nextButton.setOnClickListener {
            val intent = Intent(this, RecommendHelpActivity::class.java)
            startActivity(intent)
        }


    }
}