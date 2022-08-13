package com.dldmswo1209.chatbot.todayTodo

data class TodoItem(
    val title: String,
    var secondTitle: String,
    var state: Int
){

    companion object{
        // 상태 : 해야 할 일, 이미 한 일, 추천목록에 있는 할 일
        const val STATE_MUST_TODO = 1
        const val STATE_DID_WORK = 2
        const val STATE_RECOMMEND_WORK = 3
    }
}
