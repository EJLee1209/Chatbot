package com.dldmswo1209.chatbot.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dldmswo1209.chatbot.emotionCalendar.CalendarFragment

class CalendarFragmentAdapter(fragmentActivity: FragmentActivity) :FragmentStateAdapter(fragmentActivity){
    val firstFragmentPosition = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = Int.MAX_VALUE // 달력을 무한으로 스크롤 가능하도록 하기 위해서(그렇게 보이는 것뿐 실제로 한계가 있음)

    override fun createFragment(position: Int): Fragment {
        val calendarFragment = CalendarFragment()
        calendarFragment.pageIndex = position
        return calendarFragment
    }

}