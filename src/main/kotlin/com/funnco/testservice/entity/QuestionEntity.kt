package com.funnco.testservice.entity

import com.fasterxml.jackson.databind.JsonNode
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*

/**
 * Класс представляет сущность вопроса.
 */
@Entity
@Table(name = "question")
class QuestionEntity {

    /**
     * Уникальный идентификатор вопроса.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "question_id", nullable = false)
    var questionId: UUID? = null

    /**
     * Описание вопроса.
     */
    @Column(name = "description", nullable = false)
    var description: String? = null

    /**
     * Вес вопроса.
     */
    @Column(name = "weight", nullable = false)
    var weight: Int? = null

    /**
     * Номер вопроса в тесте.
     */
    @Column(name = "number", nullable = false)
    var number: Int? = null

    /**
     * Тип вопроса.
     */
    @ManyToOne
    var type: QuestionTypeEntity? = null

    /**
     * Тест, к которому относится вопрос.
     */
    @ManyToOne
    var test: TestEntity? = null

    /**
     * Содержание вопроса в виде JsonNode.
     */
    @Type(JsonBinaryType::class)
    @Column(name = "body", nullable = false, columnDefinition = "jsonb")
    var body: JsonNode? = null
}