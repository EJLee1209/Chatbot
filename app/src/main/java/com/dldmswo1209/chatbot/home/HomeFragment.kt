package com.dldmswo1209.chatbot.home

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.marginRight
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.chatRoom.ChatRoomActivity
import com.dldmswo1209.chatbot.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding : FragmentHomeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        checkUser()
        buttonClickEvent()
    }
    private fun checkUser(){
        // 홈 화면이 onViewCreated 될 때 sharedPreferences 에 저장되어 있는 데이터를 확인
        // name 데이터가 null 이면 AlertDialog 를 띄워서 이름을 입력 받고, 저장한다.
        val sharedPreferences = (activity as MainActivity).getSharedPreferences("user", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name",null)
        if(name == null){
            val editText = EditText(requireContext())
            val builder = AlertDialog.Builder(requireContext())

            builder.setMessage("이름을 입력해주세요.")
                .setView(editText)
                .setPositiveButton("확인",object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        if (editText.text.isEmpty()){
                            Toast.makeText(requireContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                            return
                        }
                        val editor = sharedPreferences.edit()
                        editor.apply{
                            putString("name", editText.text.toString())
                            apply()
                        }
                    }
                })
            builder.show()
        }
    }
    private fun buttonClickEvent(){
        // 버튼 클릭 이벤트 작업
        binding.chatImageButton.setOnClickListener {
            startActivity(Intent(requireContext(), ChatRoomActivity::class.java))
        }
        binding.inputTextSendButton.setOnClickListener {
            if(binding.inputEditTextView.text.toString() ==""){
                Toast.makeText(requireContext(), "메시지를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 채팅 메세지 액티비티로 전달
            val intent = Intent(requireContext(), ChatRoomActivity::class.java)
            intent.putExtra("chat", binding.inputEditTextView.text.toString())
            startActivity(intent)
            binding.inputEditTextView.text.clear()
        }
    }

}