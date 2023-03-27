package com.funnco.testservice.dto.request

import com.fasterxml.jackson.databind.JsonNode
import java.io.Serializable

/**
 * A DTO for the {@link com.funnco.testservice.entity.QuestionEntity} entity
 */
data class NewQuestionDto(
    val description: String? = null,
    val typeId: String? = null,
    val body: JsonNode? = null,
    var weight: Int? = null,
    var number: Int? = null
) : Serializable {


    /*
    * Метод валидации обхекта на корректность всех данных
    */
    fun isObjectValid(): Boolean {
        return !(description.isNullOrBlank() || typeId.isNullOrBlank() || weight == null || weight!! <= 0 || number == null || number!! <= 0)
    }
}