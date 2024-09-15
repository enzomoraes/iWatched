package com.iwatched.api.domain.repositories.projections

import java.util.*

interface UserProjection {
    val identifier: UUID
    val uid: String
    val name: String
    val username: String
    val email: String
    val image: String
    val active: Boolean
    val follows: Set<FollowsProjection>

    interface FollowsProjection {
        val identifier: UUID
        val username: String
    }
}