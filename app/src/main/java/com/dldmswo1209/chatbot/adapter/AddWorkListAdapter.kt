package com.dldmswo1209.chatbot.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dldmswo1209.chatbot.databinding.RecommendWorkItemBinding
import com.dldmswo1209.chatbot.databinding.WorkItemBinding
import com.dldmswo1209.chatbot.todayTodo.TodoItem
import com.dldmswo1209.chatbot.todayTodo.TodoItem.Companion.STATE_DID_WORK
import com.dldmswo1209.chatbot.todayTodo.TodoItem.Companion.STATE_MUST_TODO

class AddWorkListAdapter(val itemClicked: (TodoItem, isActivated: Boolean) -> (Unit)): androidx.recyclerview.widget.ListAdapter<TodoItem, AddWorkListAdapter.ViewHolder>(diffUtil)  {

    inner class ViewHolder(private val binding: RecommendWorkItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(todoItem: TodoItem){
            binding.todoTitle.text = todoItem.title
            binding.addSwitch.isChecked = todoItem.state == STATE_MUST_TODO || todoItem.state == STATE_DID_WORK

            binding.addSwitch.setOnClickListener { // 스위치 클릭 이벤트 처리
                itemClicked(todoItem, binding.addSwitch.isChecked)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecommendWorkItemBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object{
        val diffUtil = object: DiffUtil.ItemCallback<TodoItem>(){
            override fun areItemsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}