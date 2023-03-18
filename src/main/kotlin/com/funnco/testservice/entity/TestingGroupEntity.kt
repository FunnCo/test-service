package com.funnco.testservice.entity

import com.funnco.testservice.entity.composite_key.TestingGroupKey
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name="testing_group")
class TestingGroupEntity {

    @EmbeddedId
    private var id : TestingGroupKey? = null

}