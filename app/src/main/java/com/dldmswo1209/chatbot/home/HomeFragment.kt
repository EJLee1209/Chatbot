package com.dldmswo1209.chatbot.home

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.chatRoom.ChatRoomActivity
import com.dldmswo1209.chatbot.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_help.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding : FragmentHomeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        backgroundAnimation()
        characterAnimation()
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
                            checkUser()
                        }
                        val newName = editText.text.toString()
                        val testDB = Firebase.database.reference.child(newName)
                        testDB.addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){
                                    Toast.makeText(requireContext(), "이미 존재하는 이름입니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show()
                                    checkUser()
                                }else{
                                    val editor = sharedPreferences.edit()
                                    editor.apply{
                                        putString("name", newName)
                                        apply()
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
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
                (activity as MainActivity).toast("메시지를 입력해주세요")
                return@setOnClickListener
            }
            // 채팅 메세지 액티비티로 전달
            val intent = Intent(requireContext(), ChatRoomActivity::class.java)
            intent.putExtra("chat", binding.inputEditTextView.text.toString())
            startActivity(intent)
            binding.inputEditTextView.text.clear()
        }
        binding.character.setOnClickListener {
            // 캐릭터 터치시 애니메이션 실행
            YoYo.with(Techniques.Bounce)
                .duration(2000)
                .playOn(binding.character)
        }

    }
    private fun backgroundAnimation(){
        // 배경 애니메이션
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)
        anim.fillAfter = true
        binding.characterBackground.startAnimation(anim)

    }
    private fun characterAnimation(){
        // 캐릭터 애니메이션
        YoYo.with(Techniques.BounceInDown)
            .duration(2000)
            .playOn(binding.character)
    }

}