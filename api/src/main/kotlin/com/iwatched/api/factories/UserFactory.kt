package com.iwatched.api.factories

import com.iwatched.api.domain.models.User
import java.util.UUID

class UserFactory {

    companion object UserIdentifiers {
        fun createUser(name: String): User {
            return User(
                name = name,
                image = "$name.jpg",
                uid = UUID.randomUUID().toString(),
                email = "${name.split(" ")[0]}@example.com"
            )
        }

        fun createUser(name: String, identifier: UUID): User {
            return User(
                identifier = identifier,
                name = name,
                image = "$name.jpg",
                uid = UUID.randomUUID().toString(),
                email = "${name.split(" ")[0]}@example.com"
            )
        }
    }
}