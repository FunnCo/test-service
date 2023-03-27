package com.funnco.testservice.dto.request

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable

/**
 * A DTO for the {@link com.funnco.testservice.entity.SubjectEntity} entity
 */
data class NewSubjectDto(val name: String? = null) : Serializable{

    /*
    * Метод валидации обхекта на корректность всех данных
    */
    @JsonIgnore
    fun isObjectValid(): Boolean{
        return !name.isNullOrBlank()
    }
}