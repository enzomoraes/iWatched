package com.iwatched.api.domain.models

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Relationship
import java.util.UUID

data class User (
    @Id val identifier: UUID = UUID.randomUUID(),
    val uid: String,
    val name: String,
    val username: String? = name,
    val email: String,
    val image: String,
    var active: Boolean = true,
    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    var follows: MutableSet<User> = mutableSetOf()
) {

    /**
     * this method deals with both side of the relations and returns the two entities
     */
    fun follow(user: User): User {
        this.follows.add(user)
        return this
    }
}