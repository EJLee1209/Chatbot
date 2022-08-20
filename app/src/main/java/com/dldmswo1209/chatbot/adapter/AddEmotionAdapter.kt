package com.dldmswo1209.chatbot.adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.databinding.ActivityChatRoomBinding.bind
import com.dldmswo1209.chatbot.emotionCalendar.emotiondata
import kotlinx.android.synthetic.main.emotion_item.view.*
import java.text.FieldPosition

class AddEmotionAdapter(private val context: Context)
    : RecyclerView.Adapter<AddEmotionAdapter.ViewHolder>()
{
        var datas = mutableListOf<emotiondata>()

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        holder.bind(datas[position])
    }
    inner class ViewHolder(view: View):
            RecyclerView.ViewHolder(view){
                private val txtemotion: TextView=
                    itemView.findViewById(R.id.emotion_text)
                private val turimg: ImageView =
                    itemView.findViewById(R.id.turtle)
                fun bind(item: emotiondata){
                    txtemotion.text = item.emotion
                    Glide.with(itemView).load(item.img).into(turimg)
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.emotion_item,parent,false)
        view.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromuser: Boolean) {
                view.seekbar_T.text = progress.toString()
            }
            fun jud(progress: Int){

            }
        })
        return ViewHolder(view)
    }




}