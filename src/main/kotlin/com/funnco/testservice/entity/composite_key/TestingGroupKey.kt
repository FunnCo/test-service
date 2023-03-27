package com.funnco.testservice.entity.composite_key

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

/**
 * Класс, представляющий собой составной ключ для сущности TestingGroup.
 * Составной ключ содержит два поля: testId и groupId, которые объединены в один составной ключ.
 */
@Embeddable
class TestingGroupKey : Serializable {

    /**
     * Уникальный идентификатор теста, связанного с группой.
     */
    @Column(name = "test_id", nullable = false)
    lateinit var testId: UUID

    /**
     * Уникальный идентификатор группы, связанной с тестом.
     */
    @Column(name = "group_id", nullable = false)
    lateinit var groupId: UUID

    /**
     * Переопределение метода equals() для сравнения составного ключа TestingGroupKey.
     * @param other объект, с которым производится сравнение.
     * @return true, если testId и groupId в объекте совпадают с соответствующими полями текущего объекта, иначе false.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as TestingGroupKey
        if (testId != other.testId) return false
        if (groupId != other.groupId) return false
        return true
    }

    /**
     * Переопределение метода hashCode() для генерации хэш-кода составного ключа TestingGroupKey.
     * @return хэш-код объекта, рассчитанный на основе testId и groupId.
     */
    override fun hashCode(): Int {
        var result = testId?.hashCode() ?: 0
        result = 31 * result + (groupId?.hashCode() ?: 0)
        return result
    }
}