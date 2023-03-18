package com.funnco.testservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*

@Entity
@Table(name = "test")

class TestEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "test_id", nullable = false)
    var testId: UUID? = null

    @Column(name = "title", nullable = false)
    var title: String? = null

    @Column(name = "descripiton", nullable = true)
    var description: String? = null

    @Column(name = "author_id", nullable = false)
    var authorId: UUID? = null

    @ManyToOne
    var subject: SubjectEntity? = null

    @Column(name = "startDate", nullable = false)
    var startDate: Date? = null

    @Column(name = "deadlineDate", nullable = false)
    var deadlineDate: Date? = null

    @Column(name = "duration", nullable = false)
    var duration: Long? = null

}