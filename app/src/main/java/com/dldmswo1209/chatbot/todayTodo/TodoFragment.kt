package com.dldmswo1209.chatbot.todayTodo

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentTodoBinding


class TodoFragment : Fragment(R.layout.fragment_todo) {
    private lateinit var binding : FragmentTodoBinding
    private val todoAdapter = TodoListAdapter()
    private val recommendAdapter = RecommendListAdapter()
    private val recommendDetailAdapter = RecommendDetailListAdapter()
    private val todayWorkList = mutableListOf(
        TodoItem("동네를 한바퀴 산책하기","한바퀴 산책하면서 바람 맞았어요"),
        TodoItem("맛있는 간식 먹기","오늘도 수고한 나에게 맛있는 간식을 선물해요"),
        TodoItem("나른하게 낮잠자기","낮잠을 자는 것은 피로회복에 큰 도움이 돼요"),
        TodoItem("즐겁게 샤워하기","샤워를 하며 상쾌한 기분을 느꼈어요!"),
        TodoItem("친구들과 카페가기","수다를 떨며 스트레스가 사려졌어요"),
        TodoItem("집에서 푹 쉬기","하루정도 푹 쉬는 것도 좋아요")
    )
    private val recommendList = mutableListOf(
        TodoItem("숙제하기",""),
        TodoItem("일기쓰기",""),
        TodoItem("운동하기",""),
        TodoItem("코딩하기",""),
        TodoItem("샤워하기",""),
        TodoItem("카페가기",""),
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTodoBinding.bind(view)

        todoAdapter.submitList(todayWorkList)
        recommendAdapter.submitList(recommendList)
        binding.todoTodayWorkRecyclerView.adapter = todoAdapter
        binding.todoTodayWorkRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recommendRecyclerView.adapter = recommendAdapter
        val mLayoutManager = LinearLayoutManager(requireContext())
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recommendRecyclerView.layoutManager = mLayoutManager

        buttonClickEvent()
    }
    private fun buttonClickEvent(){
        binding.todoArrowRightButton.setOnClickListener {
            binding.recommendRecyclerView.isGone = true
            binding.todoRecommendTitle.isGone = true
            binding.RecommendArrowRightButton.isGone = true
            binding.todoArrowRightButton.isGone = true
            binding.todoArrowLeftButton.isVisible = true
            val layoutParams = binding.todoTodayWorkRecyclerView.layoutParams
            val height =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    680f,
                    resources.displayMetrics
                ).toInt()

            layoutParams.height = height
            binding.todoTodayWorkRecyclerView.layoutParams = layoutParams

        }
        binding.todoArrowLeftButton.setOnClickListener {
            (activity as MainActivity).refreshFragment()
        }
        recommendAdapter.setOnItemClickListener(object: OnItemClickListener{
            override fun onItemClick(
                holder: RecommendListAdapter.ViewHolder,
                view: View,
                position: Int
            ) {
                val newItem = recommendAdapter.currentList[position] ?: return
                todayWorkList.add(newItem)
                todoAdapter.submitList(todayWorkList)
                todoAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(),"할 일을 추가했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
        recommendDetailAdapter.setOnItemClickListener(object: OnDetailItemClickListener{
            override fun onItemClick(
                holder: RecommendDetailListAdapter.ViewHolder,
                view: View,
                position: Int
            ) {
                val newItem = recommendDetailAdapter.currentList[position] ?: return
                todayWorkList.add(newItem)
                todoAdapter.submitList(todayWorkList)
                todoAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(),"할 일을 추가했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
        binding.RecommendArrowRightButton.setOnClickListener {
            binding.todoSecondTitle.isGone = true
            binding.todoArrowRightButton.isGone = true
            binding.todoTodayWorkRecyclerView.isGone = true
            binding.RecommendArrowRightButton.isGone = true
            binding.todoArrowLeftButton.isVisible = true
            recommendDetailAdapter.submitList(recommendList)

            val layoutParams = binding.recommendRecyclerView.layoutParams
            layoutParams.height =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    680f,
                    resources.displayMetrics
                ).toInt()
            binding.recommendRecyclerView.apply {
                adapter = recommendDetailAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
                setHasFixedSize(true)
                setLayoutParams(layoutParams)
            }
        }

    }

}