package com.dldmswo1209.chatbot.emotionCalendar

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
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
import kotlinx.android.synthetic.main.emotion_calendar.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class EmotionCalendar : Fragment(R.layout.emotion_calendar) {
    private lateinit var binding: EmotionCalendarBinding
    private lateinit var todoDB: DatabaseReference
    private val finishedWorkList = mutableListOf<TodoItem>()
    private val finishedWorkListAdapter = FinishedWorkListAdapter()

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
        val sharedPreferences = (activity as MainActivity).getSharedPreferences("user", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name",null).toString()

        binding.finishWorkRecyclerView.adapter = finishedWorkListAdapter

        todoDB = Firebase.database.reference.child(name).child(TodoFragment.TODO_DB_PATH).child(LocalDate.now().toString())

        todoDB.addChildEventListener(listener)

        val calendarFragmentAdapter = CalendarFragmentAdapter(requireActivity())


        binding.emotionCalendar.apply {
            adapter = calendarFragmentAdapter
            orientation = ViewPager2.ORIENTATION_VERTICAL
            setCurrentItem(calendarFragmentAdapter.firstFragmentPosition, false)
        }


        val current = LocalDate.now()
        Log.d("testt", current.toString())
        val formattedDate = current.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
        binding.todayTextView.text = formattedDate
        binding.addEmotionButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).addEmotionFragment)
        }
        binding.calendarFrameLayout.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).analysisEmotionFragment)
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        todoDB.removeEventListener(listener)
        finishedWorkList.clear()
    }
}