package com.dldmswo1209.chatbot.chatRoom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dldmswo1209.chatbot.R

class ChatListAdapter : ListAdapter<ChatItem, RecyclerView.ViewHolder>(diffUtil) {
    override fun getItemViewType(position: Int): Int {
        return currentList[position].type
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
    inner class BotViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val chatTextView = view.findViewById<TextView>(R.id.botChatTextView)
        fun bind(chatItem: ChatItem){
            chatTextView.text = chatItem.chatText
        }
    }
    inner class UserViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val chatTextView = view.findViewById<TextView>(R.id.userChatTextView)
        fun bind(chatItem: ChatItem){
            chatTextView.text = chatItem.chatText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when(viewType){
            TYPE_BOT -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_bot,parent,false)
                BotViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_user,parent,false)
                UserViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(currentList[position].type){
            TYPE_BOT->{
                (holder as BotViewHolder).bind(currentList[position])
                holder.setIsRecyclable(false)
            }
            else->{
                (holder as UserViewHolder).bind(currentList[position])
                holder.setIsRecyclable(false)
            }
        }
    }

    companion object{
        val diffUtil = object: DiffUtil.ItemCallback<ChatItem>(){
            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}