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
    val episodes: Set<EpisodesProjection>

    interface FollowsProjection {
        val identifier: UUID
        val username: String
    }

    interface EpisodesProjection {
        val identifier: UUID
    }
}

interface UserProjectionTimeWatched {
    val identifier: UUID
    val uid: String
    val name: String
    val username: String
    val email: String
    val image: String
    val active: Boolean
    val follows: Set<FollowsProjection>
    val episodes: Set<EpisodesProjection>
    val timeWatched: Long

    interface FollowsProjection {
        val identifier: UUID
        val username: String
    }

    interface EpisodesProjection {
        val identifier: UUID
        val duration: Long
    }
}