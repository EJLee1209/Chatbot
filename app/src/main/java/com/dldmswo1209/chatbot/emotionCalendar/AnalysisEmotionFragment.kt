package com.dldmswo1209.chatbot.emotionCalendar

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
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

    private val allEmotionData = mutableListOf<EmotionData>()

    private val listener= object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val emotionData = snapshot.getValue(EmotionData::class.java) ?: return
            // 해당 월의 모든 감정 데이터를 가져와서

            allEmotionData.add(emotionData)

            // 감정 수치 누적
            happy += emotionData.happy
            pleasure += emotionData.pleasure
            sad += emotionData.sad
            depressed += emotionData.depressed
            anger += emotionData.depressed
            count++

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

            val dataList = ArrayList<RadarChartData>()
            dataList.add(RadarChartData(CharacteristicType.happy, happyAvg))
            dataList.add(RadarChartData(CharacteristicType.sad, sadAvg))
            dataList.add(RadarChartData(CharacteristicType.anger, angerAvg))
            dataList.add(RadarChartData(CharacteristicType.depressed, depressedAvg))
            dataList.add(RadarChartData(CharacteristicType.pleasure, pleasureAvg))

            binding.emotionRadarChart.setDataList(dataList)





        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAnalysisEmotionBinding.bind(view)
        allEmotionData.clear()

        val date = LocalDate.now().toString()
        val yearMonthDay = date.split("-")
        val yearMonth = "${yearMonthDay[0]}-${yearMonthDay[1]}"

        db = Firebase.database.reference.child((context as MainActivity).userName).child("emotionRecord").child(yearMonth)
        db.addChildEventListener(listener)

        clickedEvent()

        Handler(Looper.getMainLooper()).postDelayed({
            var max = 0
            var maxIdx = 0
            allEmotionData.forEachIndexed { index, data ->
                val positiveEmotion = data.happy + data.pleasure
                // 긍정적인 감정의 최댓값을 찾기 위함
                if(positiveEmotion > max){
                    max = positiveEmotion
                    maxIdx = index
                }
            }

            val dataList = ArrayList<BarChartData>()
            dataList.add(BarChartData(CharacteristicType.happy, allEmotionData[maxIdx].happy))
            dataList.add(BarChartData(CharacteristicType.pleasure, allEmotionData[maxIdx].pleasure))
            dataList.add(BarChartData(CharacteristicType.sad, allEmotionData[maxIdx].sad))
            dataList.add(BarChartData(CharacteristicType.depressed, allEmotionData[maxIdx].depressed))
            dataList.add(BarChartData(CharacteristicType.anger, allEmotionData[maxIdx].anger))

            binding.barChartView.setData(dataList,10,0,10)
            val date = allEmotionData[maxIdx].date.split("-")
            binding.bestDayTitle.text = "${date[1]}월 ${date[2]}일이 제일 즐거운 날이었어요!"

        }, 1000)


    }

    fun clickedEvent(){
        binding.confirmButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).calendarFragment)
        }
        binding.testButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).recommendTestFragment)
        }
    }

    override fun onDetach() {
        super.onDetach()
        db.removeEventListener(listener)
    }
}