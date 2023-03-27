package com.funnco.testservice.dto.request

import com.fasterxml.jackson.annotation.JsonIgnore
import com.funnco.testservice.utils.DateUtil
import java.io.Serializable
import java.util.*

/**
 * A DTO for the {@link com.funnco.testservice.entity.TestEntity} entity
 */
data class NewTestDto(
    val title: String? = null,
    val description: String? = null,
    val subjectId: String? = null,
    val startDate: String? = null,
    val deadlineDate: String? = null,
    val duration: Long? = null,
    val criteriaExcellent: Int? = null,
    val criteriaGood: Int? = null,
    val criteriaPass: Int? = null,
    val listOfPermittedGroups: List<String>? = null

) : Serializable {

    /*
    * Метод валидации обхекта на корректность всех данных
    */
    @JsonIgnore
    fun isObjectValid(): Boolean {
        return !(title.isNullOrBlank()
                || deadlineDate.isNullOrBlank()
                || startDate.isNullOrBlank()
                || !DateUtil.isDateInISO8601(startDate)
                || !DateUtil.isDateInISO8601(deadlineDate)
                || DateUtil.parseStringToDate(deadlineDate).before(DateUtil.parseStringToDate(startDate))
                || DateUtil.parseStringToDate(startDate).before(Date(System.currentTimeMillis()))
                || criteriaGood == null
                || criteriaPass == null
                || criteriaExcellent == null
                || subjectId == null
                || duration == null
                || listOfPermittedGroups == null)
    }
}