package com.funnco.testservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*

@Entity
@Table(name="subject")
class SubjectEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "subject_id", nullable = false)
    var subjectId: UUID? = null

    @Column(name = "name", nullable = false)
    var name: String? = null
}