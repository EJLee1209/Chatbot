package com.dldmswo1209.chatbot.chatRoom

// 서버에서 가져온 정보를 JSON 형식의 데이터에서 내가 원하는 타입의 데이터로 변환시키기 위한 DTO
class RetrofitDTO {
    data class ChatItem(val param: String)
}