package com.codeminetechnology.lumoslogicprecticalassignment.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("users/{user}")
    fun getUser(@Path("user") user: String): Call<User>

    @GET("posts")
    fun getPosts(): Call<List<Post>>
}
