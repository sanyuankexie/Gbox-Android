package com.guet.flexbox.playground

import com.guet.flexbox.data.LayoutNode
import retrofit2.Call
import retrofit2.http.GET

interface MockService {
    @GET("/data")
    fun data(): Call<Map<String, Any>>

    @GET("/layout")
    fun layout(): Call<LayoutNode>
}