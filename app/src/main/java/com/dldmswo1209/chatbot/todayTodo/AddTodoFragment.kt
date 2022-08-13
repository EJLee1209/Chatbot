package com.dldmswo1209.chatbot.todayTodo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.adapter.AddWorkListAdapter
import com.dldmswo1209.chatbot.databinding.FragmentAddTodoBinding

class AddTodoFragment : Fragment(R.layout.fragment_add_todo) {
    private val addWorkListadapter = AddWorkListAdapter { todoItem, isChecked ->
        if (isChecked) {
            (activity as MainActivity).todoFragment.addWork(todoItem)
            Toast.makeText(requireContext(), "할 일을 추가 했습니다.", Toast.LENGTH_SHORT).show()
        } else {
            (activity as MainActivity).todoFragment.removeWork(todoItem)
            Toast.makeText(requireContext(), "할 일을 제거 했습니다..", Toast.LENGTH_SHORT).show()
        }
    }

    private lateinit var binding: FragmentAddTodoBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddTodoBinding.bind(view)

        binding.backButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).todoFragment)
        }

        addWorkListadapter.submitList(recommendList)
        binding.recommendTodayWorkRecyclerView.apply {
            adapter = addWorkListadapter
            layoutManager = LinearLayoutManager(requireContext())
        }




    }


}