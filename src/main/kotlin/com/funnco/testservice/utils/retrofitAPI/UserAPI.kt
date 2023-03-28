package com.funnco.testservice.utils.retrofitAPI

import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**

Интерфейс для взаимодействия с API пользователей.
 */
interface UserAPI {

    /**
    Проверяет, существуют ли переданные группы в базе данных пользователей.
    @param groups Список идентификаторов валидируемых групп.
    @param header Заголовок авторизации в формате "Bearer {jwt}".
    @return Возвращает объект Call<Boolean>, который можно использовать для выполнения синхронного или асинхронного вызова.
     */
    @POST("/api/v2/group/validate")
    fun areGroupsValid(@Body groups: List<String>, @Header("Authorization") header: String): Call<Boolean>
}