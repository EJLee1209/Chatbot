package com.dldmswo1209.chatbot.emotionCalendar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentAddEmotionBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.emotion_calendar.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AddEmotionFragment : Fragment(R.layout.fragment_add_emotion) {
    private lateinit var binding: FragmentAddEmotionBinding
    private lateinit var calendarFragment: EmotionCalendar
    private lateinit var emotionDB: DatabaseReference
    private val listener = object: ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            val emotionData = snapshot.getValue(EmotionData::class.java) ?: return
            binding.apply {
                todayEmotionEditTextView.setText(emotionData.text)
                happySeekBar.progress = emotionData.happy
                pleasureSeekBar.progress = emotionData.pleasure
                sadSeekBar.progress = emotionData.sad
                depressedSeekBar.progress = emotionData.depressed
                angerSeekBar.progress = emotionData.anger
            }


        }

        override fun onCancelled(error: DatabaseError) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddEmotionBinding.bind(view)


        calendarFragment = EmotionCalendar()

        val current = LocalDate.now()
        Log.d("testt", current.toString())
        val formattedDate = current.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))

        binding.todayDate.text = formattedDate
        onClickListenerInit()

        val userName = (activity as MainActivity).userName
        emotionDB = Firebase.database.reference.child(userName).child("emotionRecord").child(LocalDate.now().toString())
        emotionDB.addListenerForSingleValueEvent(listener)

    }
    private fun onClickListenerInit() {
        binding.backButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(calendarFragment)
        }
        binding.checkButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(calendarFragment)
        }
        binding.happySeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.happyPercentTextView.text = "${p1}%"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        binding.pleasureSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.pleasurePercentTextView.text = "${p1}%"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        binding.sadSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.sadPercentTextView.text = "${p1}%"
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        binding.depressedSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.depressedPercentTextView.text = "${p1}%"
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        binding.angerSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                binding.angerPercentTextView.text = "${p1}%"
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        binding.checkButton.setOnClickListener {
            val text = binding.todayEmotionEditTextView.text.toString()
            val happy = binding.happySeekBar.progress
            val pleasure = binding.pleasureSeekBar.progress
            val sad = binding.sadSeekBar.progress
            val depressed = binding.depressedSeekBar.progress
            val anger = binding.angerSeekBar.progress

            val emotionData = EmotionData(LocalDate.now().toString(),text, happy, pleasure, sad, depressed, anger)
            emotionDB.setValue(emotionData)

            (activity as MainActivity).replaceFragment((activity as MainActivity).calendarFragment)
        }
    }
}