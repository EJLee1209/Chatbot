package com.dldmswo1209.chatbot.todayTodo

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentTodoBinding
import java.util.*


class TodoFragment : Fragment(R.layout.fragment_todo) {
    private lateinit var binding : FragmentTodoBinding
    private val todoAdapter = TodoListAdapter()
    private val recommendAdapter = RecommendListAdapter()
    private val recommendDetailAdapter = RecommendDetailListAdapter()
    private val todayWorkList = mutableListOf<TodoItem>()
    private val randomRecommendWorks = mutableListOf<TodoItem>()
    private var buttonClickFlag = 0 // 오른쪽 화살표 버튼 2개 중에 어떤 버튼이 눌렸는지 확인하는 플래그 변수

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTodoBinding.bind(view)

        getRandomRecommendList()
        connectRecyclerView()
        buttonClickEvent()
    }
    private fun connectRecyclerView(){
        todoAdapter.submitList(todayWorkList)
        recommendAdapter.submitList(randomRecommendWorks)
        binding.todoTodayWorkRecyclerView.apply {
            adapter = todoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        binding.recommendRecyclerView.apply {
            adapter = recommendAdapter
            val mLayoutManager = LinearLayoutManager(requireContext())
            mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            layoutManager = mLayoutManager
        }
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
    private fun todayWorkCustomLayoutParams(value: Float): ViewGroup.LayoutParams{
        val layoutParams = binding.todoTodayWorkRecyclerView.layoutParams
        val height =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                resources.displayMetrics
            ).toInt()

        layoutParams.height = height
        return layoutParams
    }
    private fun recommendWorkCustomLayoutParams(value: Float): ViewGroup.LayoutParams{
        val layoutParams = binding.recommendRecyclerView.layoutParams
        val height =
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                resources.displayMetrics
            ).toInt()

        layoutParams.height = height
        return layoutParams
    }
    private fun buttonClickEvent(){
        binding.todoArrowRightButton.setOnClickListener {
            buttonClickFlag = 1
            binding.recommendRecyclerView.isGone = true
            binding.todoRecommendTitle.isGone = true
            binding.RecommendArrowRightButton.isGone = true
            binding.todoArrowRightButton.isGone = true
            binding.todoArrowLeftButton.isVisible = true

            binding.todoTodayWorkRecyclerView.layoutParams = todayWorkCustomLayoutParams(580f)

        }
        binding.todoArrowLeftButton.setOnClickListener {
            when(buttonClickFlag){
                1 -> {
                    binding.recommendRecyclerView.isVisible = true
                    binding.todoRecommendTitle.isVisible = true
                    binding.RecommendArrowRightButton.isVisible = true
                    binding.todoArrowRightButton.isVisible = true
                    binding.todoArrowLeftButton.isGone = true

                    binding.todoTodayWorkRecyclerView.layoutParams = todayWorkCustomLayoutParams(410f)
                    buttonClickFlag = 0
                }
                2 -> {
                    binding.todoSecondTitle.isVisible = true
                    binding.todoArrowRightButton.isVisible = true
                    binding.todoTodayWorkRecyclerView.isVisible = true
                    binding.RecommendArrowRightButton.isVisible = true
                    binding.todoArrowLeftButton.isGone = true

                    binding.recommendRecyclerView.apply {
                        adapter = recommendAdapter
                        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        setHasFixedSize(true)
                        setLayoutParams(recommendWorkCustomLayoutParams(105f))
                    }
                    buttonClickFlag = 0
                }
            }

        }
        recommendAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(
                holder: RecommendListAdapter.ViewHolder,
                view: View,
                position: Int
            ) {
                addWork(position)
            }
        })
        recommendDetailAdapter.setOnItemClickListener(object: OnDetailItemClickListener{
            override fun onItemClick(
                holder: RecommendDetailListAdapter.ViewHolder,
                view: View,
                position: Int
            ) {
                addWork(position)
            }
        })
        binding.RecommendArrowRightButton.setOnClickListener {
            buttonClickFlag = 2
            binding.todoSecondTitle.isGone = true
            binding.todoArrowRightButton.isGone = true
            binding.todoTodayWorkRecyclerView.isGone = true
            binding.RecommendArrowRightButton.isGone = true
            binding.todoArrowLeftButton.isVisible = true
            recommendDetailAdapter.submitList(randomRecommendWorks)


            binding.recommendRecyclerView.apply {
                adapter = recommendDetailAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
                setHasFixedSize(true)
                setLayoutParams(recommendWorkCustomLayoutParams(580f))
            }
        }

    }
    private fun addWork(position: Int){
        val newItem = recommendAdapter.currentList[position] ?: return
        if(todayWorkList.contains(newItem)) {
            Toast.makeText(requireContext(), "이미 할 일 목록에 추가했습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        todayWorkList.add(newItem)
        todoAdapter.submitList(todayWorkList)
        todoAdapter.notifyDataSetChanged()
        Toast.makeText(requireContext(),"할 일을 추가했습니다.", Toast.LENGTH_SHORT).show()
    }

}