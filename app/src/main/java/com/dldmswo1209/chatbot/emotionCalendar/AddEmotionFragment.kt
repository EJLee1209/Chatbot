package com.dldmswo1209.chatbot.emotionCalendar

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.adapter.AddEmotionAdapter
import com.dldmswo1209.chatbot.databinding.FragmentAddEmotionBinding
import kotlinx.android.synthetic.main.fragment_add_emotion.*


class AddEmotionFragment : Fragment(R.layout.fragment_add_emotion) {
    private lateinit var binding: FragmentAddEmotionBinding
    private lateinit var calendarFragment: calendarFragment
    lateinit var addEmotionAdapter:AddEmotionAdapter
    val datas = mutableListOf<emotiondata>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddEmotionBinding.bind(view)
        calendarFragment = calendarFragment()

        binding.backButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(calendarFragment)
        }
        binding.checkButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(calendarFragment)
        }
        val today = arguments?.getString("today")
        binding.todayDate.setText(today)
        //리사이클러뷰
        initRecycler()

    }

    private fun initRecycler() {
        val activity = context as Activity
        addEmotionAdapter = AddEmotionAdapter(context as Activity)
        emotionRecView.adapter = addEmotionAdapter

        datas.apply{
            add(emotiondata(img = R.drawable.emotion_addrec, emotion = "행복"
            ))
            add(emotiondata(img = R.drawable.emotion_addrec, emotion = "기쁨"
            ))
            add(emotiondata(img = R.drawable.emotion_addrec, emotion = "슬픔"
            ))
            add(emotiondata(img = R.drawable.emotion_addrec, emotion = "우울"
            ))
            add(emotiondata(img = R.drawable.emotion_addrec, emotion = "분노"
            ))
        }
        addEmotionAdapter.datas = datas
        addEmotionAdapter.notifyDataSetChanged()
        emotionRecView.addItemDecoration(VerticalItemDecorator(20))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }
}

