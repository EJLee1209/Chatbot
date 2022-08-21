package com.dldmswo1209.chatbot.emotionCalendar

import android.os.Bundle
import android.provider.Settings.Global.getString
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.adapter.CalendarAdapter
import com.dldmswo1209.chatbot.databinding.FragmentCalendarBinding
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class calendarFragment : Fragment(R.layout.fragment_calendar) {
    private lateinit var binding : FragmentCalendarBinding
    lateinit var selectedDate: LocalDate
    private lateinit var calendar: Calendar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCalendarBinding.bind(view)
        CalendarUtil.selectedDate = LocalDate.now()
        setMonthView()
        binding.goAddEmotion.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).addEmotionFragment)
        }
        binding.preBtn.setOnClickListener{
            selectedDate = selectedDate.minusMonths(1)
            setMonthView()
        }
        binding.nextBtn.setOnClickListener{
            selectedDate = selectedDate.plusMonths(1)
            setMonthView()
        }
        //오늘 있던일
        val todayemotion = App.prefs.getString("text","")
        binding.todayemotionedt.text = todayemotion


    }

    private fun setMonthView(){
        binding.monthYearText.text = monthYearFromDate(selectedDate)
        val dayList = dayInMonthArray(selectedDate)
        val adapter = CalendarAdapter(dayList)
        var manager: RecyclerView.LayoutManager = GridLayoutManager(context,7)
        binding.recyclerView.layoutManager=manager
        binding.recyclerView.adapter=adapter
    }

    private  fun monthYearFromDate(date: LocalDate): String{
        var formatter = DateTimeFormatter.ofPattern("MM월 yyyy")
        return date.format(formatter)
    }

    private fun dayInMonthArray(date: LocalDate): ArrayList<LocalDate?>{
        var dayList = ArrayList<LocalDate?>()
        var yearMonth = YearMonth.from(date)
        var lastDay = yearMonth.lengthOfMonth()
        var firstDay = CalendarUtil.selectedDate.withDayOfMonth(1)
        var dayOfWeek = firstDay.dayOfWeek.value

        for(i in 1..41){
            if(i<=dayOfWeek||i>(lastDay+dayOfWeek)){
                dayList.add(null)
            }else{
                dayList.add(LocalDate.of(selectedDate.year,
                selectedDate.monthValue,i-dayOfWeek))
            }
        }
        return dayList
    }


}

















