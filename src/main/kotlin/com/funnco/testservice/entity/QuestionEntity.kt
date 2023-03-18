package com.funnco.testservice.entity

import com.fasterxml.jackson.databind.JsonNode
import com.funnco.testservice.converter.JsonConverter
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import java.util.*

@Entity
@Table(name = "question")
class QuestionEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "question_id", nullable = false)
    var questionId: UUID? = null

    @ManyToOne
    var type: QuestionTypeEntity? = null

    @ManyToOne
    var test: TestEntity? = null

    @Convert(converter = JsonConverter::class)
    @Column(name = "body", nullable = false, columnDefinition = "json")
    var body: JsonNode? = null
}