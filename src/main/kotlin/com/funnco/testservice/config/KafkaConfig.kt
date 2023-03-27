package com.funnco.testservice.config

import com.funnco.testservice.model.AnswerReportMessage
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer


/**
 * Класс, представляющий конфигурацию для Kafka.
 */
@Configuration
class KafkaConfig {

    /**
     * Метод, описывающий и создающий фабрику продюсеров для отправки сообщений в Kafka.
     * @return Фабрику продюсеров для отправки сообщений в Kafka.
     */
    @Bean
    fun answerReportProducerFactory(): ProducerFactory<String, AnswerReportMessage> {
        val configProperties = mutableMapOf<String, Any>()
        configProperties[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "broker:9092"
        configProperties[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        configProperties[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java
        return DefaultKafkaProducerFactory(configProperties)
    }

    /**
     * Метод, возвращающий шаблон Kafka для отправки сообщений.
     * @return шаблон Kafka для отправки сообщений.
     */
    @Bean
    fun answerReportMessageTemplate(): KafkaTemplate<String, AnswerReportMessage> {
        return KafkaTemplate(answerReportProducerFactory())
    }
}