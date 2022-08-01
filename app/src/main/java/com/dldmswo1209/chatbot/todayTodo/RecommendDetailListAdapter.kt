package com.dldmswo1209.chatbot.todayTodo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dldmswo1209.chatbot.databinding.RecommendItemDetailBinding


interface OnDetailItemClickListener {
    fun onItemClick(holder: RecommendDetailListAdapter.ViewHolder, view: View, position: Int)
}

class RecommendDetailListAdapter : androidx.recyclerview.widget.ListAdapter<TodoItem, RecommendDetailListAdapter.ViewHolder>(diffUtil), OnDetailItemClickListener  {

    private lateinit var listener: OnDetailItemClickListener

    inner class ViewHolder(private val binding: RecommendItemDetailBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(todoItem: TodoItem){
            binding.recommendTitle.text = todoItem.title
            binding.root.setOnClickListener { view->

                listener.onItemClick(ViewHolder(binding), view, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecommendItemDetailBinding.inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun onItemClick(holder: ViewHolder, view: View, position: Int) {
        listener.onItemClick(holder, view, position)
    }
    fun setOnItemClickListener(listener: OnDetailItemClickListener){
        this.listener = listener
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