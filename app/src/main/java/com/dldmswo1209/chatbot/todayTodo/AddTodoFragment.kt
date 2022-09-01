package com.dldmswo1209.chatbot.todayTodo

import android.content.Context
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
import com.dldmswo1209.chatbot.todayTodo.TodoItem.Companion.STATE_MUST_TODO
import com.dldmswo1209.chatbot.todayTodo.TodoItem.Companion.STATE_RECOMMEND_WORK
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_add_todo.*
import java.time.LocalDate

class AddTodoFragment : Fragment(R.layout.fragment_add_todo) {
    private lateinit var binding: FragmentAddTodoBinding
    private lateinit var addWorkListAdapter: AddWorkListAdapter
    private lateinit var userName: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddTodoBinding.bind(view)

        val sharedPreferences = (activity as MainActivity).getSharedPreferences("user", Context.MODE_PRIVATE)
        userName = sharedPreferences.getString("name","").toString()

        val todoDB = Firebase.database.reference
            .child(userName)
            .child(TodoFragment.TODO_DB_PATH)
            .child(LocalDate.now().toString())

        addWorkListAdapter = AddWorkListAdapter { todoItem, isChecked ->
            if (isChecked) {
                (activity as MainActivity).todoFragment.addWork(todoItem)
                todoItem.state = STATE_MUST_TODO
                // DB 에 추가하는 작업
                todoDB.child(todoItem.title).setValue(todoItem)

                Toast.makeText(requireContext(), "할 일을 추가 했습니다.", Toast.LENGTH_SHORT).show()
            } else {
                (activity as MainActivity).todoFragment.removeWork(todoItem)
                todoItem.state = STATE_RECOMMEND_WORK
                // DB 에서 제거하는 작업
                todoDB.child(todoItem.title).removeValue()

                Toast.makeText(requireContext(), "할 일을 제거 했습니다..", Toast.LENGTH_SHORT).show()
            }
        }


        binding.backButton.setOnClickListener {
            (activity as MainActivity).replaceFragment((activity as MainActivity).todoFragment)
        }


        addWorkListAdapter.submitList(recommendList)
        binding.recommendTodayWorkRecyclerView.apply {
            adapter = addWorkListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }





    }


}