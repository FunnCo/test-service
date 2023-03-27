package com.funnco.testservice.dto.request

/**
 * A DTO for the {@link com.funnco.testservice.entity.TestEntity} entity
 */
import com.fasterxml.jackson.annotation.JsonIgnore
import com.funnco.testservice.dto.universal.QuestionDto
import com.funnco.testservice.utils.DateUtil
import java.io.Serializable
import java.util.*

data class SolvedTestDto(
    var answers: List<QuestionDto>? = null,
    var startSolvingDateTime: String? = null,
    var finishSolvingDateTime: String? = null,
) : Serializable {


    /*
     * Метод валидации обхекта на корректность всех данных
     */
    @JsonIgnore
    fun isObjectValid(): Boolean {
        return !(startSolvingDateTime == null
                || !DateUtil.isDateInISO8601(startSolvingDateTime!!)
                || finishSolvingDateTime == null
                || !DateUtil.isDateInISO8601(finishSolvingDateTime!!)
                || DateUtil.parseStringToDate(finishSolvingDateTime!!).time - DateUtil.parseStringToDate(
            startSolvingDateTime!!
        ).time <= 1000
                || answers == null
                || answers!!.any { !it.isObjectValid() })
    }
}