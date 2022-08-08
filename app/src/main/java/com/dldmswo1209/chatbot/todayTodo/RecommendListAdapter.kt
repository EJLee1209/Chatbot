package com.dldmswo1209.chatbot.todayTodo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dldmswo1209.chatbot.databinding.RecommendItemBinding

interface OnItemClickListener {
    fun onItemClick(holder: RecommendListAdapter.ViewHolder, view: View, position: Int)
}

class RecommendListAdapter: androidx.recyclerview.widget.ListAdapter<TodoItem, RecommendListAdapter.ViewHolder>(diffUtil), OnItemClickListener  {

    private lateinit var listener: OnItemClickListener

    inner class ViewHolder(private val binding: RecommendItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(todoItem: TodoItem){
            binding.recommendTitle.text = todoItem.title
            binding.root.setOnClickListener { view->
                listener.onItemClick(ViewHolder(binding), view, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecommendItemBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    override fun onItemClick(holder: ViewHolder, view: View, position: Int) {
        listener.onItemClick(holder,view, position)
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