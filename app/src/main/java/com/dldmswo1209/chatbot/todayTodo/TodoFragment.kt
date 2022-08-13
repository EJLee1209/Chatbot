package com.dldmswo1209.chatbot.todayTodo

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.adapter.TodoListAdapter
import com.dldmswo1209.chatbot.databinding.FragmentTodoBinding
import com.dldmswo1209.chatbot.todayTodo.TodoItem.Companion.STATE_DID_WORK
import com.dldmswo1209.chatbot.todayTodo.TodoItem.Companion.STATE_MUST_TODO
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.util.*

/**
 * 할 일 추천 목록 DB 저장 구조
 * 유저 이름 -> recommendWorks - > 현재 날짜(년,월,일) -> todoItem 의 title(문제점이 있을 것 같은데 지금은 이게 최선인듯) -> TodoItem() 객체로 저장
 */

/**
 * 할 일 추천 목록 DB 저장 알고리즘
 * 1. 할 일 전체 리스트에서 랜덤으로 5개 정도 뽑아서 오늘의 할 일 추천 리스트를 만든다.
 * 2. DB 에 추천 리스트를 저장한다.
 * 3. 앱을 실행 시킬 때 DB 에서 오늘의 할 일이 DB 상에 존재하는지 확인하고, 존재하면 DB 에서 바로 가져오면 되고,
 *    존재하지 않으면, 1번부터 순차적으로 진행한다.
 */

class TodoFragment : Fragment(R.layout.fragment_todo) {
    private lateinit var binding : FragmentTodoBinding
    private lateinit var todoDB: DatabaseReference
    private lateinit var userName: String
    private val listener = object: ChildEventListener{
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val todoItem = snapshot.getValue(TodoItem::class.java) ?: return

            randomRecommendWorks.add(todoItem)
            todoAdapter.submitList(randomRecommendWorks)
            todoAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onChildRemoved(snapshot: DataSnapshot) {}

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

        override fun onCancelled(error: DatabaseError) {}
    }

    private val todoAdapter = TodoListAdapter { todoItem, isChecked ->
        if(isChecked){
            todoItem.state = STATE_DID_WORK
            todoDB.child(todoItem.title).setValue(todoItem)
        }else{
            todoItem.state = STATE_MUST_TODO
            todoDB.child(todoItem.title).setValue(todoItem)
        }

    }
    val randomRecommendWorks = mutableListOf<TodoItem>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTodoBinding.bind(view)

        // 초기화 작업
        randomRecommendWorks.clear()

        val sharedPreferences = (activity as MainActivity).getSharedPreferences("user", Context.MODE_PRIVATE)
        userName = sharedPreferences.getString("name","").toString()
        todoDB = Firebase.database.reference.child(userName).child(TODO_DB_PATH).child(LocalDate.now().toString())

        todoDB.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                }else{
                    getRandomRecommendList()
                    saveRandomRecommendListToDB(randomRecommendWorks)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        todoDB.addChildEventListener(listener)

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
    }
    private fun getRandomRecommendList(){
        // 할 일 추천 리스트에서 랜덤 5개를 뽑음
        val set = mutableSetOf<TodoItem>()

        while(set.size < 5 && randomRecommendWorks.size < 5){
            val random = Random().nextInt(recommendList.size)
            set.add(recommendList[random])
        }
        set.forEach{ randomItem ->
            randomRecommendWorks.add(randomItem)
        }

    }
    private fun saveRandomRecommendListToDB(randomRecommendWorks: MutableList<TodoItem>){
        randomRecommendWorks.forEach{ todoItem ->
            todoDB.child(todoItem.title).setValue(todoItem)
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

    companion object{
        const val TODO_DB_PATH = "recommendWorks"
    }
}