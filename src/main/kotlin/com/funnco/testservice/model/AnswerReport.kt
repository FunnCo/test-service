package com.funnco.testservice.model

import java.io.Serializable
import java.time.LocalDateTime
import java.util.Date

/**
* Класс, представляющий отчет по ответам студента на тест.
* @param studentId идентификатор студента
* @param testId идентификатор теста
* @param checkResults результаты проверки ответов студента на тест. Порядок проверки соответствует questionNumber вопроса из базы данных, даже если вопросы перемешаны.
* @param mark оценка за тест
* @param solveDate дата, когда студент завершил тест
* @param timeSolving время, затраченное на прохождение теста в миллисекундах
 */
data class AnswerReport (
    var studentId: String,
    var testId: String,
    var checkResults: List<Boolean>, // Порядок проверки соответствует questionNumber вопроса из базы данных, даже если вопросы перемешаны
    var mark: Int,
    var solveDate: Date,
    var timeSolving: Long
) : Serializable

/**
 * Сообщение, которое отправляется в Kafka.
 * @param sendTime Время отправки сообщения.
 * @param answerReport Сериализуемый объект AnswerReport, который должен быть отправлен в Kafka.
 */
data class AnswerReportMessage(val sendTime: LocalDateTime, val answerReport: AnswerReport) : Serializable