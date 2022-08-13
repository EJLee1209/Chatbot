package com.dldmswo1209.chatbot.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dldmswo1209.chatbot.databinding.WorkItemBinding
import com.dldmswo1209.chatbot.todayTodo.TodoItem

class TodoListAdapter: androidx.recyclerview.widget.ListAdapter<TodoItem, TodoListAdapter.ViewHolder>(diffUtil)  {

    inner class ViewHolder(private val binding: WorkItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(todoItem: TodoItem){
            binding.todoTitle.text = todoItem.title
            binding.todoSecondTitle.text = todoItem.secondTitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(WorkItemBinding.inflate(LayoutInflater.from(parent.context),parent, false))
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