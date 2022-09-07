package com.dldmswo1209.chatbot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dldmswo1209.chatbot.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        YoYo.with(Techniques.FadeInDown)
            .duration(3500)
            .playOn(binding.title)

        YoYo.with(Techniques.FadeInDown)
            .duration(3500)
            .playOn(binding.subTitle)

        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        },DURATION)

    }
    companion object{
        private const val DURATION : Long = 2000
    }
}