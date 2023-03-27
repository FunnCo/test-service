package com.funnco.testservice.dto.response

import java.io.Serializable
import java.util.*

/**
 * A DTO for the {@link com.funnco.testservice.entity.QuestionTypeEntity} entity
 */
data class ResponseTypeDto(val typeId: UUID? = null, val name: String? = null) : Serializable