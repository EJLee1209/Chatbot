package com.dldmswo1209.chatbot.todayTodo

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dldmswo1209.chatbot.databinding.RecommendItemBinding

class RecommendListAdapter: androidx.recyclerview.widget.ListAdapter<TodoItem, RecommendListAdapter.ViewHolder>(diffUtil)  {

    inner class ViewHolder(private val binding: RecommendItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(todoItem: TodoItem){
            binding.recommendTitle.text = todoItem.title
            binding.recommendPlusButton.setOnClickListener {
                // 할 일 추가
                Log.d("testt","버튼 클릭!")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecommendItemBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object: DiffUtil.ItemCallback<TodoItem>(){
            override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}