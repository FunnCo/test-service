package com.funnco.testservice.dto.universal

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable
import java.util.*

/**
 * A DTO for the {@link com.funnco.testservice.entity.SubjectEntity} entity
 */
data class SubjectDto(
    val subjectId: String? = null,
    val name: String? = null
) : Serializable {

    @JsonIgnore
    fun isObjectValid(): Boolean{
        return !(subjectId.isNullOrBlank() || name.isNullOrBlank())
    }

}
