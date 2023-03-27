package com.funnco.testservice.dto.request

import java.io.Serializable

/**
 * A DTO for the {@link com.funnco.testservice.entity.QuestionTypeEntity} entity
 */
data class NewQuestionTypeDto(val name: String? = null) : Serializable {

    /*
    * Метод валидации обхекта на корректность всех данных
    */
    fun isObjectValid(): Boolean {
        return !name.isNullOrBlank()
    }
}