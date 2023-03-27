package com.funnco.testservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*

/**
 * Класс представляет сущность типа вопроса.
 */
@Entity
@Table(name = "question_type")
class QuestionTypeEntity {

    /**
     * Уникальный идентификатор типа вопроса.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "type_id", nullable = false)
    var typeId: UUID? = null

    /**
     * Название типа
     */
    @Column(name = "name", nullable = false, unique = true)
    var name: String? = null
}