package com.funnco.testservice.utils

import com.funnco.testservice.utils.retrofitAPI.UserAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**

 * Объект-обертка для создания экземпляров API-клиентов с помощью Retrofit.
 * Использует единственный экземпляр Retrofit для создания API-клиентов.
 * Базовый URL для запросов установлен в "http://auth-api:8080/"
 * и конвертер сериализации/десериализации JSON-объектов GsonConverterFactory.
 */
object RetrofitHandler {

    /**
     * Экземпляр Retrofit для создания API-клиентов.
     * */
    private val retrofitInstance = Retrofit.Builder()
        .baseUrl("http://auth-api:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    /**
     * API-клиент для выполнения запросов к AuthAPI.
     * */
    val userAPI by lazy {
        retrofitInstance.create(UserAPI::class.java)
    }
}