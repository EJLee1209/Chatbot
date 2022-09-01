package com.dldmswo1209.chatbot.emotionCalendar

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.adapter.CalendarAdapter
import com.dldmswo1209.chatbot.databinding.FragmentCalendarBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment(R.layout.fragment_calendar) {
    private lateinit var binding: FragmentCalendarBinding

    private val TAG = javaClass.simpleName
    lateinit var mContext: Context
    var pageIndex = 0
    lateinit var currentDate: Date

    companion object {
        var instance: CalendarFragment? = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            mContext = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCalendarBinding.bind(view)

        initView(view)
    }
    fun initView(view: View){
        pageIndex -= (Int.MAX_VALUE / 2)
        // 날짜 적용
        val date = Calendar.getInstance().run {
            add(Calendar.MONTH, pageIndex)
            time
        }
        currentDate = date

        // 포맷 적용
        var datetime: String = SimpleDateFormat(
            mContext.getString(R.string.calendar_year_month_format),
            Locale.KOREA
        ).format(date.time)
        binding.calendarYearMonthTextView.setText(datetime)
        val calendarLayout = view.findViewById<LinearLayoutCompat>(R.id.calendarLayout)
        val gridLayoutManager = GridLayoutManager(context, 7, LinearLayoutManager.VERTICAL, false)
        val calendarAdapter = CalendarAdapter(requireContext(), calendarLayout, currentDate) {
            (activity as MainActivity).date = it
            (activity as MainActivity).replaceFragment((activity as MainActivity).addEmotionFragment)
        }

        binding.calendarView.adapter = calendarAdapter
        binding.calendarView.layoutManager = gridLayoutManager


    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}