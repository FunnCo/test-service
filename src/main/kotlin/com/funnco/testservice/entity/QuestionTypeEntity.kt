package com.funnco.testservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*

@Entity
@Table(name = "question_type")
class QuestionTypeEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "type_id", nullable = false)
    var typeId: UUID? = null

    @Column(name = "name", nullable = false)
    var name: String? = null
}