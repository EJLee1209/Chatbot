package com.dldmswo1209.chatbot.emotionCalendar

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.adapter.CalendarFragmentAdapter
import com.dldmswo1209.chatbot.adapter.FinishedWorkListAdapter
import com.dldmswo1209.chatbot.databinding.EmotionCalendarBinding
import com.dldmswo1209.chatbot.todayTodo.TodoFragment
import com.dldmswo1209.chatbot.todayTodo.TodoItem
import com.dldmswo1209.chatbot.todayTodo.TodoItem.Companion.STATE_DID_WORK
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class EmotionCalendar : Fragment(R.layout.emotion_calendar) {
    private lateinit var binding: EmotionCalendarBinding
    private lateinit var todoDB: DatabaseReference
    private lateinit var todayEmotionDB: DatabaseReference
    private var isConfirmAnalysis = false
    private var isConfirmRecommend = false
    private val finishedWorkList = mutableListOf<TodoItem>()
    private val finishedWorkListAdapter = FinishedWorkListAdapter()
    private val customColors = mutableListOf(
        ColorTemplate.rgb("#EACB82"),
        ColorTemplate.rgb("#DCE8E4"),
        ColorTemplate.rgb("#089371"),
        ColorTemplate.rgb("#3A5F62"),
        ColorTemplate.rgb("#F8FBF6")
    )
    private val allEmotionData = mutableListOf<EmotionData>()
    private val todayEmotionListener = object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val emotionData = snapshot.getValue(EmotionData::class.java) ?: return
            allEmotionData.add(emotionData)

            if(emotionData.date == LocalDate.now().toString()){
                binding.apply {
                    todayEventTextView.text = emotionData.text
                    happyPercent.text = "행복${emotionData.happy}%"
                    pleasurePercent.text = "기쁨${emotionData.pleasure}%"
                    sadPercent.text = "슬픔${emotionData.sad}%"
                    depressedPercent.text = "우울${emotionData.depressed}%"
                    angerPercent.text = "분노${emotionData.anger}%"

                    val positiveValue = ArrayList<PieEntry>()
                    positiveValue.add(PieEntry(emotionData.happy.toFloat(), ""))
                    positiveValue.add(PieEntry(emotionData.pleasure.toFloat(), ""))

                    val negativeValue = ArrayList<PieEntry>()
                    negativeValue.add(PieEntry(emotionData.sad.toFloat(), ""))
                    negativeValue.add(PieEntry(emotionData.depressed.toFloat(), ""))
                    negativeValue.add(PieEntry(emotionData.anger.toFloat(), ""))

                    val positiveDataSet = PieDataSet(positiveValue, "")
                    positiveDataSet.sliceSpace = 2f
                    positiveDataSet.selectionShift = 1.5f
                    positiveDataSet.colors = customColors

                    val positiveData = PieData(positiveDataSet)
                    positiveData.setValueTextColor(Color.WHITE)
                    positiveData.setValueTextSize(10f)

                    val negativeDataSet = PieDataSet(negativeValue, "")
                    negativeDataSet.sliceSpace = 2f
                    negativeDataSet.selectionShift = 1.5f
                    negativeDataSet.colors = customColors
                    val negativeData = PieData(negativeDataSet)
                    negativeData.setValueTextColor(Color.WHITE)
                    negativeData.setValueTextSize(10f)

                    positiveEmotionPieChart.legend.isEnabled = false
                    positiveEmotionPieChart.data = positiveData
                    positiveEmotionPieChart.isActivated = true

                    negativeEmotionPieChart.legend.isEnabled = false
                    negativeEmotionPieChart.data = negativeData
                    negativeEmotionPieChart.isActivated = true

                    binding.panelTextView.isGone = true
                    binding.addEmotionButton.text = "수정하기"
                }

            }

        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }

    private val listener: ChildEventListener = object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val workItem = snapshot.getValue(TodoItem::class.java)?:return
            if(workItem.state == STATE_DID_WORK){
                finishedWorkList.add(workItem)
                finishedWorkListAdapter.submitList(finishedWorkList)
                finishedWorkListAdapter.notifyDataSetChanged()

            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EmotionCalendarBinding.bind(view)

        initPieChart()
        initDB()
        clickEvent()
        checkLastDay()
        initCalendar()
        checkRecentEmotion()

        binding.finishWorkRecyclerView.adapter = finishedWorkListAdapter
    }
    private fun initCalendar(){
        val calendarFragmentAdapter = CalendarFragmentAdapter(requireActivity())
        binding.emotionCalendar.apply {
            adapter = calendarFragmentAdapter
            orientation = ViewPager2.ORIENTATION_VERTICAL
            setCurrentItem(calendarFragmentAdapter.firstFragmentPosition, false)
        }

    }
    private fun checkLastDay(){
        val calendar = Calendar.getInstance()
        val date = LocalDate.now().toString()
        val yearMonthDay = date.split("-")
        val year = yearMonthDay[0].toInt()
        val month = yearMonthDay[1].toInt()
        val day = yearMonthDay[2].toInt()

        calendar.set(year,month-1,1)
        val lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        if(day == lastDay && !isConfirmAnalysis){
            // 오늘이 월의 마지막 날이라면 감정 분석 화면을 보여줌
            // 앱 실행 시 최초 한번만 보여주기 위해서 isConfirm 이라는 변수를 사용
            (activity as MainActivity).replaceFragment((activity as MainActivity).analysisEmotionFragment)
            isConfirmAnalysis = true
        }
    }
    private fun checkRecentEmotion(){
        Handler(Looper.getMainLooper()).postDelayed({
            // 데이터베이스에서 불러오는 시간때문에 임의로 딜레이를 줘서 수행했다
            // 좋은 방법은 아니지만, 일단 작동은 잘 된다.

            // 저장된 감정의 수가 14개 미만 이거나, 이미 권유를 받은 경우 return
            if(allEmotionData.size < 14 || isConfirmRecommend) return@postDelayed
            val startIdx = allEmotionData.size - 14

            // 최근 14일의 감정데이터를 가져와서 긍정적인 감정과 부정적인 감정의 합을 비교한다
            for(i in startIdx until allEmotionData.size){
                val positiveEmotion = allEmotionData[i].pleasure + allEmotionData[i].happy
                val negativeEmotion = allEmotionData[i].sad + allEmotionData[i].depressed + allEmotionData[i].anger
                if(positiveEmotion > negativeEmotion) return@postDelayed // 긍정적인 감정이 더 큰 날이 있었으면 return
            }
            // 여기까지 수행이 된다면 최근 14일간 부정적인 감정이 지속 되었다는 것
            // 자가진단을 권유하는 화면을 보여준다.
            // 권유를 계속하면 앱 사용에 문제가 있으니 한번만 보여주도록 한다. 앱을 껐다켜면 다시 권유한다.
            (activity as MainActivity).replaceFragment((activity as MainActivity).recommendTestFragment)
            isConfirmRecommend = true

        }, 1000)
    }

    private fun initPieChart(){
        binding.apply {
            positiveEmotionPieChart.setUsePercentValues(false)
            positiveEmotionPieChart.description.isEnabled = false

            //드래그 후 놨을때 애니메이션 멈춤 속도 조절 0에 가까울수록 바로 멈춤
            positiveEmotionPieChart.dragDecelerationFrictionCoef = 0.9f
            positiveEmotionPieChart.isDrawHoleEnabled = false
            positiveEmotionPieChart.setHoleColor(Color.WHITE)

            negativeEmotionPieChart.setUsePercentValues(false)
            negativeEmotionPieChart.description.isEnabled = false

            //드래그 후 놨을때 애니메이션 멈춤 속도 조절 0에 가까울수록 바로 멈춤
            negativeEmotionPieChart.dragDecelerationFrictionCoef = 0.9f
            negativeEmotionPieChart.isDrawHoleEnabled = false
            negativeEmotionPieChart.setHoleColor(Color.WHITE)
        }
    }
    private fun initDB(){
        val userName = (activity as MainActivity).userName
        val date = LocalDate.now().toString()
        val yearMonthDay = date.split("-")
        val yearMonth = "${yearMonthDay[0]}-${yearMonthDay[1]}"
        todoDB = Firebase.database.reference.child(userName).child(TodoFragment.TODO_DB_PATH).child(LocalDate.now().toString())
        todayEmotionDB = Firebase.database.reference.child(userName).child("emotionRecord").child(yearMonth)

        todoDB.addChildEventListener(listener)
        todayEmotionDB.addChildEventListener(todayEmotionListener)
    }
    private fun clickEvent(){
        val current = LocalDate.now()

        val formattedDate = current.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
        binding.todayTextView.text = formattedDate
        binding.addEmotionButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).addEmotionFragment)
        }
        binding.todayEmotionRightArrowButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).addEmotionFragment)
        }
        binding.finishWorkRightArrowButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).todoFragment)
            (activity as MainActivity).todoButton.performClick()
        }
        binding.analEmotionButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).analysisEmotionFragment)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        todoDB.removeEventListener(listener)
        todayEmotionDB.removeEventListener(todayEmotionListener)
        finishedWorkList.clear()
    }
}