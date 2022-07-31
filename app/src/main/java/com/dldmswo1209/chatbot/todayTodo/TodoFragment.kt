package com.dldmswo1209.chatbot.todayTodo

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.FragmentTodoBinding


class TodoFragment : Fragment(R.layout.fragment_todo) {
    private lateinit var binding : FragmentTodoBinding
    private val todoAdapter = TodoListAdapter()
    private val recommendAdapter = RecommendListAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTodoBinding.bind(view)

        todoAdapter.submitList(
            mutableListOf(
                TodoItem("동네를 한바퀴 산책하기","한바퀴 산책하면서 바람 맞았어요"),
                TodoItem("맛있는 간식 먹기","오늘도 수고한 나에게 맛있는 간식을 선물해요"),
                TodoItem("나른하게 낮잠자기","낮잠을 자는 것은 피로회복에 큰 도움이 돼요"),
                TodoItem("즐겁게 샤워하기","샤워를 하며 상쾌한 기분을 느꼈어요!"),
                TodoItem("친구들과 카페가기","수다를 떨며 스트레스가 사려졌어요"),
                TodoItem("집에서 푹 쉬기","하루정도 푹 쉬는 것도 좋아요")
            )
        )
        recommendAdapter.submitList(
            mutableListOf(
                TodoItem("숙제하기",""),
                TodoItem("숙제하기",""),
                TodoItem("숙제하기",""),
                TodoItem("숙제하기",""),
                TodoItem("숙제하기",""),
                TodoItem("숙제하기",""),
            )
        )
        binding.todoTodayWorkRecyclerView.adapter = todoAdapter
        binding.todoTodayWorkRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recommendRecyclerView.adapter = recommendAdapter
        val mLayoutManager = LinearLayoutManager(requireContext())
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.recommendRecyclerView.layoutManager = mLayoutManager

        buttonClickEvent()
    }
    private fun buttonClickEvent(){
        binding.RecommendArrowRightButton.setOnClickListener {

        }
        binding.todoArrowRightButton.setOnClickListener {
            binding.recommendRecyclerView.isGone = true
            binding.todoRecommendTitle.isGone = true
            binding.RecommendArrowRightButton.isGone = true
            binding.todoArrowRightButton.isGone = true
            binding.todoArrowLeftButton.isVisible = true
            val layoutPrams = binding.todoTodayWorkRecyclerView.layoutParams
            val height =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    680f,
                    resources.displayMetrics
                ).toInt()

            layoutPrams.height = height
            binding.todoTodayWorkRecyclerView.layoutParams = layoutPrams

        }
        binding.todoArrowLeftButton.setOnClickListener {
            binding.recommendRecyclerView.isVisible = true
            binding.todoRecommendTitle.isVisible = true
            binding.RecommendArrowRightButton.isVisible = true
            binding.todoArrowRightButton.isVisible = true
            binding.todoArrowLeftButton.isGone = true
            val layoutPrams = binding.todoTodayWorkRecyclerView.layoutParams
            val height =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    448F,
                    resources.displayMetrics
                ).toInt()

            layoutPrams.height = height
            binding.todoTodayWorkRecyclerView.layoutParams = layoutPrams

        }
    }

}