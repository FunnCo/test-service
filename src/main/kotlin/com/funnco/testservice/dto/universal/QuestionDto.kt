package com.funnco.testservice.dto.universal

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import java.io.Serializable

/**
 * A DTO for the {@link com.funnco.testservice.entity.QuestionEntity} entity
 */
data class QuestionDto(
    val questionId: String? = null,
    val description: String? = null,
    val typeId: String? = null,
    val body: JsonNode? = null,
    val number: Int? = null
) : Serializable
{
    @JsonIgnore
    fun isObjectValid(): Boolean{
        return !(questionId.isNullOrBlank() || description.isNullOrBlank() || typeId.isNullOrBlank() || body == null)
    }

}