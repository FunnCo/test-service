package com.funnco.testservice.entity

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import java.util.*

/**
 * Класс, представляющий сущность "Тест" (TestEntity) для хранения информации о тестах в базе данных.
 */
@Entity
@Table(name = "test")
class TestEntity {

    /**
     * Уникальный идентификатор теста.
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "test_id", nullable = false)
    var testId: UUID? = null

    /**
     * Название теста.
     */
    @Column(name = "title", nullable = false)
    var title: String? = null

    /**
     * Описание теста.
     */
    @Column(name = "descripiton", nullable = true)
    var description: String? = null

    /**
     * Уникальный идентификатор автора теста.
     */
    @Column(name = "author_id", nullable = false)
    var authorId: UUID? = null

    /**
     * Предмет, к которому относится тест.
     */
    @ManyToOne
    var subject: SubjectEntity? = null

    /**
     * Дата начала теста.
     */
    @Column(name = "startDate", nullable = false)
    var startDate: Date? = null

    /**
     * Дата окончания теста.
     */
    @Column(name = "deadlineDate", nullable = false)
    var deadlineDate: Date? = null

    /**
     * Продолжительность теста (в миллисекундах).
     */
    @Column(name = "duration", nullable = false)
    var duration: Long? = null

    /**
     * Критерий "отлично" (в процентах).
     */
    @Column(name = "criteria_excellent", nullable = false)
    var criteriaExcellent: Int? = null

    /**
     * Критерий "хорошо" (в процентах).
     */
    @Column(name = "criteria_good", nullable = false)
    var criteriaGood: Int? = null

    /**
     * Критерий "неудовлетворительно" (в процентах).
     */
    @Column(name = "criteria_pass", nullable = false)
    var criteriaPass: Int? = null
}