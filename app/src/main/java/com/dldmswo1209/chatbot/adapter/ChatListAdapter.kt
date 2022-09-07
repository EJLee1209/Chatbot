package com.dldmswo1209.chatbot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.chatRoom.ChatItem
import com.dldmswo1209.chatbot.chatRoom.TYPE_BOT
import com.dldmswo1209.chatbot.chatRoom.TYPE_BOT_RECOMMEND

class ChatListAdapter(val onItemClicked: (Boolean) -> Unit) : ListAdapter<ChatItem, RecyclerView.ViewHolder>(
    diffUtil
) {
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
    inner class BotRecommendViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val chatTextView = view.findViewById<TextView>(R.id.botRecommendChatTextView)
        private val noButton = view.findViewById<TextView>(R.id.noButton)
        private val yesButton = view.findViewById<TextView>(R.id.yesButton)
        fun bind(chatItem: ChatItem){
            chatTextView.text = chatItem.chatText
            noButton.setOnClickListener {
                onItemClicked(false)
            }
            yesButton.setOnClickListener {
                onItemClicked(true)
            }
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
            TYPE_BOT_RECOMMEND ->{
                view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_bot_recommend,parent,false)
                BotRecommendViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item_user,parent,false)
                UserViewHolder(view)
            }

        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(currentList[position].type){
            TYPE_BOT ->{
                (holder as BotViewHolder).bind(currentList[position])
                holder.setIsRecyclable(false)
            }
            TYPE_BOT_RECOMMEND ->{
                (holder as BotRecommendViewHolder).bind(currentList[position])
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