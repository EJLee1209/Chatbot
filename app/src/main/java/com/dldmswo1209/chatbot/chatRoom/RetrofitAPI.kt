package com.dldmswo1209.chatbot.chatRoom

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface RetrofitAPI {
    @GET("/echo_call/{chatText}") // 서버에 GET 요청을 할 주소 입력
    fun getAIReply(@Path("chatText") chatText: String) : Call<JsonObject> // 입력으로 chatText 를 서버로 넘기고, 서버에서 답장을 가져옴.
}