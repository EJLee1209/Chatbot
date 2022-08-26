package com.dldmswo1209.chatbot.emotionCalendar

data class EmotionData(
    val date: String,
    val text: String,
    val happy: Int,
    val pleasure: Int,
    val sad: Int,
    val depressed: Int,
    val anger: Int
){
    constructor(): this("","",0,0,0,0,0)
}
