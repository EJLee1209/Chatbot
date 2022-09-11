package com.dldmswo1209.chatbot.emotionCalendar

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dldmswo1209.chatbot.*
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.chatRoom.ChatItem
import com.dldmswo1209.chatbot.chatRoom.TYPE_USER
import com.dldmswo1209.chatbot.databinding.FragmentAnalysisEmotionBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_todo.view.*
import kotlinx.coroutines.*
import java.time.LocalDate

class AnalysisEmotionFragment : Fragment(R.layout.fragment_analysis_emotion) {
    private lateinit var binding: FragmentAnalysisEmotionBinding
    private lateinit var db: DatabaseReference
    private var happy = 0
    private var pleasure = 0
    private var sad = 0
    private var depressed = 0
    private var anger = 0
    private var count = 0
    private var happyAvg = 0
    private var pleasureAvg = 0
    private var sadAvg = 0
    private var depressedAvg = 0
    private var angerAvg = 0
    private var max = 0
    private var maxIdx = 0
    private val allEmotionData = mutableListOf<EmotionData>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAnalysisEmotionBinding.bind(view)
        allEmotionData.clear()

        val date = LocalDate.now().toString()
        val yearMonthDay = date.split("-")
        val yearMonth = "${yearMonthDay[0]}-${yearMonthDay[1]}"

        binding.analysisEmotionSubTitle.text = "${yearMonthDay[1].toInt()}월 동안 이런 감정을 느꼈어요!"

        db = Firebase.database.reference.child((context as MainActivity).userName).child("emotionRecord").child(yearMonth)

        clickedEvent()

        // 코루틴을 사용해서 스레드로 데이터 가져오는 작업 하기
        // 기존에는 데이터를 가져오는데 시간이 걸려서 임의로 딜레이를 주고
        // 차트를 초기화 했는데, 이렇게 하면 딜레이를 줄 필요가 없음
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                db.get().addOnCompleteListener {
                    it.result.children.forEach { data->
                        // db 에 저장되어있는 현재 월의 모든 감정기록을 하나씩 가져와서
                        val emotionData = data.getValue(EmotionData::class.java)?:return@addOnCompleteListener
                        // 뷰 초기화(그래프 그리기, 프로그레스바, 평균 감정수치)
                        allEmotionData.add(emotionData)
                    }
                    initView()

                }.addOnCanceledListener {
                    (activity as MainActivity).toast("데이터를 가져오는데 실패했습니다.")
                }
            }
        }
    }

    fun initView(){
        // 감정 수치 누적
        allEmotionData.forEach {
            happy += it.happy
            pleasure += it.pleasure
            sad += it.sad
            depressed += it.depressed
            anger += it.anger
            count++
        }

        // 감정 각각의 평균을 구함
        happyAvg = happy/count
        pleasureAvg = pleasure/count
        sadAvg = sad/count
        depressedAvg = depressed/count
        angerAvg = anger/count

        // 프로그레스 바에 표시
        binding.happyProgressBar.progress = happyAvg
        binding.pleasureProgressBar.progress = pleasureAvg
        binding.sadProgressBar.progress = sadAvg
        binding.depressedProgressBar.progress = depressedAvg
        binding.angerProgressBar.progress = angerAvg

        // 몇 %인지 표시
        binding.happyPercent.text = "$happyAvg%"
        binding.pleasurePercent.text = "$pleasureAvg%"
        binding.sadPercent.text = "$sadAvg%"
        binding.depressedPercent.text = "$depressedAvg%"
        binding.angerPercent.text = "$angerAvg%"

        // 레이다차트 데이터 생성
        val dataList = ArrayList<RadarAnimationChartData>()
        dataList.add(RadarAnimationChartData(CharacteristicType.happy, happyAvg))
        dataList.add(RadarAnimationChartData(CharacteristicType.sad, sadAvg))
        dataList.add(RadarAnimationChartData(CharacteristicType.anger, angerAvg))
        dataList.add(RadarAnimationChartData(CharacteristicType.depressed, depressedAvg))
        dataList.add(RadarAnimationChartData(CharacteristicType.pleasure, pleasureAvg))

        // 레이다차트에 데이터 전달
        binding.emotionRadarChart.setDataList(dataList)

        allEmotionData.forEachIndexed { index, data ->
            val positiveEmotion = data.happy + data.pleasure
            // 긍정적인 감정의 최댓값을 찾기 위함
            if(positiveEmotion > max){
                max = positiveEmotion
                maxIdx = index
            }
        }

        // 바 차트 데이터 생성
        val barChartDataList = ArrayList<BarChartData>()
        barChartDataList.add(BarChartData(CharacteristicType.happy, allEmotionData[maxIdx].happy))
        barChartDataList.add(BarChartData(CharacteristicType.pleasure, allEmotionData[maxIdx].pleasure))
        barChartDataList.add(BarChartData(CharacteristicType.sad, allEmotionData[maxIdx].sad))
        barChartDataList.add(BarChartData(CharacteristicType.depressed, allEmotionData[maxIdx].depressed))
        barChartDataList.add(BarChartData(CharacteristicType.anger, allEmotionData[maxIdx].anger))

        // 바 차트에 데이터 전달
        binding.barChartView.setData(barChartDataList,10,0,10)
        // 제일 즐거운 날 날짜 표시
        val date = allEmotionData[maxIdx].date.split("-")

        binding.bestDayTitle.text = "${date[1]}월 ${date[2]}일이 제일 즐거운 날이었어요!"
    }

    private fun clickedEvent(){
        binding.confirmButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).calendarFragment)
        }
        binding.testButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).recommendTestFragment)
        }
        binding.bestDayRightArrowButton.setOnClickListener {
            (activity as MainActivity).date = allEmotionData[maxIdx].date
            (activity as MainActivity).replaceFragment((activity as MainActivity).addEmotionFragment)
        }
    }

}