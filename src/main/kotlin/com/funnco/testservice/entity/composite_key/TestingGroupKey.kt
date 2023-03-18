package com.funnco.testservice.entity.composite_key

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.*

@Embeddable
class TestingGroupKey : Serializable {

    @Column(name = "test_id", nullable = false)
    private var testId : UUID? = null

    @Column(name = "group_id", nullable = false)
    private var groupId: UUID? = null

}