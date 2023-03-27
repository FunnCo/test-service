package com.funnco.testservice.utils.retrofitAPI

import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface UserAPI {
    @POST("/api/v2/group/validate")
    fun areGroupsValid(@Body groups: List<String>, @Header("Authorization") header: String): Call<Boolean>

}