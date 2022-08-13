package com.dldmswo1209.chatbot.todayTodo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.adapter.TodoListAdapter
import com.dldmswo1209.chatbot.databinding.FragmentTodoBinding
import java.util.*


class TodoFragment : Fragment(R.layout.fragment_todo) {
    private lateinit var binding : FragmentTodoBinding
    private val todoAdapter = TodoListAdapter()
    private val randomRecommendWorks = mutableListOf<TodoItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTodoBinding.bind(view)

        getRandomRecommendList()
        connectRecyclerView()
        buttonClickEvent()
    }
    private fun connectRecyclerView(){
        // 오늘의 할 일 추천 목록 리사이클러뷰 연결
        todoAdapter.submitList(randomRecommendWorks)
        binding.recommendTodayWorkRecyclerView.apply {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        // 오늘의 할 일 추천 더 받기 목록 리사이클러뷰 연결

    }
    private fun getRandomRecommendList(){
        // 할 일 추천 리스트에서 랜덤 8개를 뽑음
        val set = mutableSetOf<TodoItem>()

        while(set.size < 8 && randomRecommendWorks.size < 8){
            val random = Random().nextInt(recommendList.size)
            set.add(recommendList[random])
        }
        set.forEach{
            randomRecommendWorks.add(it)
        }
    }
    private fun buttonClickEvent(){
        binding.addTodayWorkButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).addTodoFragment)
        }
    }
    fun addWork(todoItem: TodoItem){
        randomRecommendWorks.add(todoItem)
    }
    fun removeWork(todoItem: TodoItem){
        randomRecommendWorks.remove(todoItem)
    }


}