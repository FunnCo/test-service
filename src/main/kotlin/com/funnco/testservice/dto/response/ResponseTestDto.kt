package com.funnco.testservice.dto.response

import java.io.Serializable
import java.util.*

/**
 * A DTO for the {@link com.funnco.testservice.entity.TestEntity} entity
 */
data class ResponseTestDto(
    val testId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val authorId: String? = null,
    val subjectName: String? = null,
    val startDate: Date? = null,
    val deadlineDate: Date? = null,
    val duration: Long? = null,
) : Serializable