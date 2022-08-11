package com.dldmswo1209.chatbot.chatRoom

data class ChatItem(
    val chatText: String,
    val type: Int
){
    constructor() : this("", 1){

    }
}
