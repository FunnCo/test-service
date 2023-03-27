package com.funnco.testservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*


/**
 * Класс представляет сущность предмета.
 */
@Entity
@Table(name="subject")
class SubjectEntity {

    /**
     * Уникальный идентификатор типа вопроса.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "subject_id", nullable = false)
    var subjectId: UUID? = null

    /**
     * Название предмета
     */
    @Column(name = "name", nullable = false, unique = true)
    var name: String? = null
}