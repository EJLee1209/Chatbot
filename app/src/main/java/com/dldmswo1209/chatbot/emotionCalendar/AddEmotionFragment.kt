package com.dldmswo1209.chatbot.emotionCalendar

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.CharacteristicType
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.RadarChartData
import com.dldmswo1209.chatbot.databinding.FragmentAddEmotionBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.emotion_calendar.*
import kotlinx.android.synthetic.main.fragment_add_emotion.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AddEmotionFragment : Fragment(R.layout.fragment_add_emotion) {
    private lateinit var binding: FragmentAddEmotionBinding
    private lateinit var calendarFragment: EmotionCalendar
    private lateinit var emotionDB: DatabaseReference
    private var presentEmotion = 0
    private var happy = 0
    private var pleasure = 0
    private var sad = 0
    private var depressed = 0
    private var anger = 0

    private val listener = object: ValueEventListener{
        override fun onDataChange(snapshot: DataSnapshot) {
            val emotionData = snapshot.getValue(EmotionData::class.java)

            if(emotionData == null){
                binding.todayEmotionEditTextView.text.clear()
                happy = 0
                pleasure = 0
                sad = 0
                depressed = 0
                anger = 0

                emotionSeekBar.progress = pleasure

                val dataList = ArrayList<RadarChartData>()
                dataList.add(RadarChartData(CharacteristicType.happy, happy))
                dataList.add(RadarChartData(CharacteristicType.sad, sad))
                dataList.add(RadarChartData(CharacteristicType.anger, anger))
                dataList.add(RadarChartData(CharacteristicType.depressed, depressed))
                dataList.add(RadarChartData(CharacteristicType.pleasure, pleasure))

                binding.emotionRadarChart.setDataList(dataList)

                return
            }

            binding.apply {
                // 오늘 기록한 감정이 있다면 가져와서 보여주기
                todayEmotionEditTextView.setText(emotionData.text)
                happy = emotionData.happy
                pleasure = emotionData.pleasure
                sad = emotionData.sad
                depressed = emotionData.depressed
                anger = emotionData.anger

                emotionSeekBar.progress = pleasure

                val dataList = ArrayList<RadarChartData>()
                dataList.add(RadarChartData(CharacteristicType.happy, happy))
                dataList.add(RadarChartData(CharacteristicType.sad, sad))
                dataList.add(RadarChartData(CharacteristicType.anger, anger))
                dataList.add(RadarChartData(CharacteristicType.depressed, depressed))
                dataList.add(RadarChartData(CharacteristicType.pleasure, pleasure))

                binding.emotionRadarChart.setDataList(dataList)

            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddEmotionBinding.bind(view)
        val clickedDate = (activity as MainActivity).date
        val userName = (activity as MainActivity).userName

        calendarFragment = EmotionCalendar()

        if(clickedDate == "" || clickedDate == LocalDate.now().toString()) {
            val current = LocalDate.now()
            val formattedDate = current.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))

            binding.todayDate.text = formattedDate
            emotionDB = Firebase.database.reference.child(userName).child("emotionRecord").child(LocalDate.now().toString())
            binding.checkButton.isVisible = true
            binding.addEmotionTitle.text = "오늘의 기분 추가하기"
            binding.todayEmotionEditTextView.isEnabled = true
            binding.todayEmotionSubTitle.text = "오늘의 감정 기록하기"
            binding.todayEmotionThirdTitle.text = "기록할 감정"
            binding.emotionSeekBar.isEnabled = true

        }else{
            val dateList = clickedDate.split("-")
            binding.todayDate.text = "${dateList[0]}년 ${dateList[1]}월 ${dateList[2]}일"
            (activity as MainActivity).date=""
            emotionDB = Firebase.database.reference.child(userName).child("emotionRecord").child(clickedDate)
            binding.checkButton.isVisible = false
            binding.addEmotionTitle.text = ""
            binding.todayEmotionEditTextView.isEnabled = false
            binding.todayEmotionSubTitle.text = "감정 그래프"
            binding.todayEmotionThirdTitle.text = "기록한 감정"
            binding.emotionSeekBar.isEnabled = false

        }
        onClickListenerInit()

        emotionDB.addListenerForSingleValueEvent(listener)
        binding.emotionRadioGroup.check(R.id.pleasureButton)
        presentEmotion = R.id.pleasureButton // 다른 라디오 버튼을 클릭하기 이전의 버튼의 아이디를 저장함



        binding.emotionRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.pleasureButton ->{
                    savePreviousEmotion()
                    updateSeekbar()
                }
                R.id.happyButton -> {
                    savePreviousEmotion()
                    updateSeekbar()
                }
                R.id.sadButton -> {
                    savePreviousEmotion()
                    updateSeekbar()
                }
                R.id.depressedButton ->{
                    savePreviousEmotion()
                    updateSeekbar()
                }
                R.id.angerButton -> {
                    savePreviousEmotion()
                    updateSeekbar()
                }
            }
        }

    }
    private fun savePreviousEmotion(){
        // 눌린 라디오 버튼 이전의 버튼의 아이디가 presentEmotion 에 저장되어 있는 상태
        // 현재 눌린 버튼이 행복 버튼이고, 이전 버튼은 기쁨 버튼이라고 가정하겠음.
        // 1. 현재 seekbar.progress 를 가져와서 기쁨 변수에 저장
        // 2. presentEmotion 에 행복 버튼 아이디를 저장(최신화)
        // 3 ~ 4 번은 updateSeekbar() 함수 수행
        // 3. 만약 행복 변수의 값이 0이 아니라면, seekbar.progress = 행복 변수 값
        // 4. 행복 변수 값이 0이면 seekbar.progress = 0

        // 체크 버튼클릭시 즉시 savePreviousEmotion 함수를 수행 후, Emotion 객체를 생성하고, DB 에 저장한다.
        when(presentEmotion){
            R.id.pleasureButton ->{
                pleasure = binding.emotionSeekBar.progress
            }
            R.id.happyButton ->{
                happy = binding.emotionSeekBar.progress
            }
            R.id.sadButton ->{
                sad = binding.emotionSeekBar.progress
            }
            R.id.depressedButton->{
                depressed = binding.emotionSeekBar.progress
            }
            R.id.angerButton->{
                anger = binding.emotionSeekBar.progress
            }
        }
        presentEmotion = binding.emotionRadioGroup.checkedRadioButtonId
    }
    private fun updateSeekbar(){
        when(presentEmotion){
            R.id.pleasureButton ->{
                binding.emotionSeekBar.progress = pleasure
            }
            R.id.happyButton ->{
                binding.emotionSeekBar.progress = happy
            }
            R.id.sadButton->{
                binding.emotionSeekBar.progress = sad
            }
            R.id.depressedButton->{
                binding.emotionSeekBar.progress = depressed
            }
            R.id.angerButton->{
                binding.emotionSeekBar.progress = anger
            }
        }
    }


    private fun onClickListenerInit() {
        binding.backButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(calendarFragment)
        }
        binding.checkButton.setOnClickListener {
            (activity as MainActivity).replaceFragment(calendarFragment)
        }
        binding.emotionSeekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar, progress: Int, p2: Boolean) {
                binding.emotionValueTextView.text = "$progress%"

                when(binding.emotionRadioGroup.checkedRadioButtonId) {
                    R.id.happyButton -> {
                        happy = progress
                        updateGraph()
                    }
                    R.id.sadButton -> {
                        sad = progress
                        updateGraph()
                    }
                    R.id.angerButton -> {
                        anger = progress
                        updateGraph()
                    }
                    R.id.depressedButton -> {
                        depressed = progress
                        updateGraph()
                    }
                    R.id.pleasureButton -> {
                        pleasure = progress
                        updateGraph()

                    }
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        binding.checkButton.setOnClickListener {
            // 즉시 저장
            savePreviousEmotion()

            // 체크 버튼 클릭시 감정 저장
            val text = binding.todayEmotionEditTextView.text.toString()
//            // 객체로 저장
            val emotionData = EmotionData(LocalDate.now().toString(),text, happy, pleasure, sad, depressed, anger)
            emotionDB.setValue(emotionData)

            // 화면 전환
            (activity as MainActivity).replaceFragment((activity as MainActivity).calendarFragment)
        }
    }
    private fun updateGraph(){
        val dataList = ArrayList<RadarChartData>()
        dataList.add(RadarChartData(CharacteristicType.happy, happy))
        dataList.add(RadarChartData(CharacteristicType.sad, sad))
        dataList.add(RadarChartData(CharacteristicType.anger, anger))
        dataList.add(RadarChartData(CharacteristicType.depressed, depressed))
        dataList.add(RadarChartData(CharacteristicType.pleasure, pleasure))
        Log.d("testt",pleasure.toString())

        binding.emotionRadarChart.setDataList(dataList)
    }

    override fun onDestroy() {
        super.onDestroy()
        emotionDB.removeEventListener(listener)

    }
}