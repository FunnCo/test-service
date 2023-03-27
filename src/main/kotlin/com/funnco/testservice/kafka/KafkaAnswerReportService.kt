package com.funnco.testservice.kafka

import com.funnco.testservice.model.AnswerReport
import com.funnco.testservice.model.AnswerReportMessage
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * Сервис, отвечающий за отправление сообщений о выполненных тестах в брокер.
 */
@Service
class KafkaAnswerReportService(private val kafkaTemplate: KafkaTemplate<String, AnswerReportMessage>) {


    /**
     * Отправляет сообщение в Kafka в топик "TestService.TestAnswer.save", содержащее отчет о выполненном тесте
     * и время отправки сообщения.
     * @param answerReport Сериализуемый объект AnswerReport, который должен быть отправлен в Kafka.
     */
    fun sendAnswerReport(answerReport: AnswerReport) {
        val message = AnswerReportMessage(LocalDateTime.now(), answerReport)
        kafkaTemplate.send("TestService.TestAnswer.save", message)
    }

}