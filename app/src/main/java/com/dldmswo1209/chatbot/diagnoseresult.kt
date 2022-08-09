package com.dldmswo1209.chatbot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.dldmswo1209.chatbot.databinding.ActivityDiagnoseresultBinding

private  lateinit var binding:ActivityDiagnoseresultBinding

class diagnoseresult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diagnoseresult)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_diagnoseresult)

        binding.helpbtn.setOnClickListener{
            val intent = Intent(this, diagnosis2::class.java)
        }

    }
}