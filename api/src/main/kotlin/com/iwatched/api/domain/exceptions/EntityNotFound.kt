package com.iwatched.api.domain.exceptions

import kotlin.reflect.KClass

class EntityNotFound(message: String, private val entityClass: KClass<*>) : RuntimeException(message) {

    override fun toString(): String {
        return "$message, Entity class: ${entityClass.simpleName}"
    }
}
