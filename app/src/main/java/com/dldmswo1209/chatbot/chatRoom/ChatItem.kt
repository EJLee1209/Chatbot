package com.dldmswo1209.chatbot.chatRoom

data class ChatItem(
    val chatText: String,
    val type: Int
){
    constructor() : this("", 1){} // DB 에서 객체 형태로 가져오려면 생성자가 필요함
}
