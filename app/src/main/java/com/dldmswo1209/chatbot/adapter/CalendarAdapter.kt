package com.dldmswo1209.chatbot.adapter

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.dldmswo1209.chatbot.MainActivity
import com.dldmswo1209.chatbot.R
import com.dldmswo1209.chatbot.emotionCalendar.EmotionData
import com.dldmswo1209.chatbot.emotionCalendar.FurangCalendar
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.calendar_item.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// 높이를 구하는데 필요한 LinearLayout과 FurangCalender를 사용할 때 필요한 date를 받는다.
class CalendarAdapter(val context: Context, val calendarLayout: LinearLayoutCompat, val date: Date, val itemClicked: () ->(Unit)) :
    RecyclerView.Adapter<CalendarAdapter.CalendarItemHolder>() {

    private val TAG = javaClass.simpleName
    var dataList: ArrayList<Int> = arrayListOf()

    // FurangCalendar을 이용하여 날짜 리스트 세팅
    var furangCalendar: FurangCalendar = FurangCalendar(date)
    init {
        furangCalendar.initBaseCalendar()
        dataList = furangCalendar.dateList
    }

    override fun onBindViewHolder(holder: CalendarItemHolder, position: Int) {

        // list_item_calendar 높이 지정
        val h = calendarLayout.height / 6
        holder.itemView.layoutParams.height = h

        holder?.bind(dataList[position], position, context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarItemHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.calendar_item, parent, false)
        return CalendarItemHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    inner class CalendarItemHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var itemCalendarDateText: TextView = itemView!!.dateTextView
        var emotionColor: ImageView = itemView!!.emotionColor

        fun bind(data: Int, position: Int, context: Context) {
            val firstDateIndex = furangCalendar.prevTail
            val lastDateIndex = dataList.size - furangCalendar.nextHead - 1
            val month = String.format("%02d",date.month+1)
            val year = date.year+1900

            Log.d("testt", "year : $year")
            Log.d("testt", "month : ${month}")

            // 날짜 표시
            itemCalendarDateText.setText(data.toString())

            // 오늘 날짜 처리
            var dateString: String = SimpleDateFormat("dd", Locale.KOREA).format(date)
            var dateInt = dateString.toInt()
            if (dataList[position] == dateInt) {
                itemCalendarDateText.setTypeface(itemCalendarDateText.typeface, Typeface.BOLD)
            }

//             현재 월의 1일 이전, 현재 월의 마지막일 이후 값의 텍스트를 회색처리
            if (position < firstDateIndex || position > lastDateIndex) {
                itemCalendarDateText.setTextAppearance(R.style.LightColorTextViewStyle)
            }
            else if (position == 0 || position%7==0 || (position+1)%7==0){
                itemCalendarDateText.setTextAppearance(R.style.weekendColor)
            }

            if(position in firstDateIndex..lastDateIndex){
                // 현재 월의 1일 ~ 마지막 일 만 처리
                val db = Firebase.database.reference.child((context as MainActivity).userName).child("emotionRecord")
                val listener = object: ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        val emotion = snapshot.getValue(EmotionData::class.java)?:return
                        val date = emotion.date.split('-')

                        if(date[0] == year.toString() && date[1] == month.toString() && date[2] == String.format("%02d",data)){
                            val positiveEmotion = emotion.happy + emotion.pleasure
                            val negativeEmotion = emotion.anger + emotion.sad + emotion.depressed
                            if(positiveEmotion >= negativeEmotion){
                                emotionColor.setImageResource(R.drawable.emotion_background_circle_good)
                            }else{
                                emotionColor.setImageResource(R.drawable.emotion_background_circle_bad)
                            }
                        }
                    }

                    override fun onChildChanged(
                        snapshot: DataSnapshot,
                        previousChildName: String?
                    ) {
                        notifyDataSetChanged()
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {}

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                    override fun onCancelled(error: DatabaseError) {}
                }
                db.addChildEventListener(listener)

            }


            itemView.setOnClickListener {
                itemClicked()
            }
        }

    }
}