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
                    happyPercent.text = "??????${emotionData.happy}%"
                    pleasurePercent.text = "??????${emotionData.pleasure}%"
                    sadPercent.text = "??????${emotionData.sad}%"
                    depressedPercent.text = "??????${emotionData.depressed}%"
                    angerPercent.text = "??????${emotionData.anger}%"

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
                    binding.addEmotionButton.text = "????????????"
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
            // ????????? ?????? ????????? ???????????? ?????? ?????? ????????? ?????????
            // ??? ?????? ??? ?????? ????????? ???????????? ????????? isConfirm ????????? ????????? ??????
            (activity as MainActivity).replaceFragment((activity as MainActivity).analysisEmotionFragment)
            isConfirmAnalysis = true
        }
    }
    private fun checkRecentEmotion(){
        Handler(Looper.getMainLooper()).postDelayed({
            // ???????????????????????? ???????????? ??????????????? ????????? ???????????? ?????? ????????????
            // ?????? ????????? ????????????, ?????? ????????? ??? ??????.

            // ????????? ????????? ?????? 14??? ?????? ?????????, ?????? ????????? ?????? ?????? return
            if(allEmotionData.size < 14 || isConfirmRecommend) return@postDelayed
            val startIdx = allEmotionData.size - 14

            // ?????? 14?????? ?????????????????? ???????????? ???????????? ????????? ???????????? ????????? ?????? ????????????
            for(i in startIdx until allEmotionData.size){
                val positiveEmotion = allEmotionData[i].pleasure + allEmotionData[i].happy
                val negativeEmotion = allEmotionData[i].sad + allEmotionData[i].depressed + allEmotionData[i].anger
                if(positiveEmotion > negativeEmotion) return@postDelayed // ???????????? ????????? ??? ??? ?????? ???????????? return
            }
            // ???????????? ????????? ????????? ?????? 14?????? ???????????? ????????? ?????? ???????????? ???
            // ??????????????? ???????????? ????????? ????????????.
            // ????????? ???????????? ??? ????????? ????????? ????????? ????????? ??????????????? ??????. ?????? ???????????? ?????? ????????????.
            (activity as MainActivity).replaceFragment((activity as MainActivity).recommendTestFragment)
            isConfirmRecommend = true

        }, 1000)
    }

    private fun initPieChart(){
        binding.apply {
            positiveEmotionPieChart.setUsePercentValues(false)
            positiveEmotionPieChart.description.isEnabled = false

            //????????? ??? ????????? ??????????????? ?????? ?????? ?????? 0??? ??????????????? ?????? ??????
            positiveEmotionPieChart.dragDecelerationFrictionCoef = 0.9f
            positiveEmotionPieChart.isDrawHoleEnabled = false
            positiveEmotionPieChart.setHoleColor(Color.WHITE)

            negativeEmotionPieChart.setUsePercentValues(false)
            negativeEmotionPieChart.description.isEnabled = false

            //????????? ??? ????????? ??????????????? ?????? ?????? ?????? 0??? ??????????????? ?????? ??????
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

        val formattedDate = current.format(DateTimeFormatter.ofPattern("yyyy??? MM??? dd???"))
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