package com.dldmswo1209.chatbot.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.chatRoom.ChatRoomActivity
import com.dldmswo1209.chatbot.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding : FragmentHomeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        buttonClickEvent()
    }
    private fun buttonClickEvent(){
        binding.chatImageButton.setOnClickListener {
            startActivity(Intent(requireContext(), ChatRoomActivity::class.java))
        }
        binding.inputTextSendButton.setOnClickListener {
            if(binding.inputEditTextView.text.toString() ==""){
                Toast.makeText(requireContext(), "메시지를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(requireContext(), ChatRoomActivity::class.java)
            intent.putExtra("chat", binding.inputEditTextView.text.toString())
            startActivity(intent)
            binding.inputEditTextView.text.clear()
        }
    }

}