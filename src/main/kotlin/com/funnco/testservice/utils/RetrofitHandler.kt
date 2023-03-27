package com.funnco.testservice.utils

import com.funnco.testservice.utils.retrofitAPI.UserAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHandler {

    private val retrofitInstance = Retrofit.Builder().baseUrl("http://auth-api:8080/").addConverterFactory(GsonConverterFactory.create()).build()

    val userAPI by lazy {
        retrofitInstance.create(UserAPI::class.java)
    }
}