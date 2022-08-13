package com.dldmswo1209.chatbot.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.WorkItemBinding
import com.dldmswo1209.chatbot.todayTodo.TodoItem
import com.dldmswo1209.chatbot.todayTodo.TodoItem.Companion.STATE_DID_WORK

class TodoListAdapter(val itemClicked: (TodoItem, isChecked: Boolean)->(Unit)): androidx.recyclerview.widget.ListAdapter<TodoItem, TodoListAdapter.ViewHolder>(diffUtil)  {
    inner class ViewHolder(private val binding: WorkItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(todoItem: TodoItem){
            binding.todoTitle.text = todoItem.title
            binding.todoSecondTitle.text = todoItem.secondTitle
            binding.todoCheckbox.isChecked = todoItem.state == STATE_DID_WORK

            binding.todoCheckbox.setOnClickListener{ // 체크박스 클릭 이벤트
                itemClicked(todoItem, binding.todoCheckbox.isChecked)
            }

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
                return oldItem.title == newItem.title
            }

            override fun areContentsTheSame(oldItem: TodoItem, newItem: TodoItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}