package com.funnco.testservice.entity

import com.funnco.testservice.entity.composite_key.TestingGroupKey
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

/**
 * Класс, представляющий сущность допуска группы к тесту.
 */
@Entity
@Table(name="testing_group")
class TestingGroupEntity {

    /**
     * Уникальный идентификатор теста.
     */
    @EmbeddedId
    lateinit var id : TestingGroupKey

}