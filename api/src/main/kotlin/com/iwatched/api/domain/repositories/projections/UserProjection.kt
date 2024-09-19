package com.iwatched.api.domain.repositories.projections

import java.util.*

data class UserProjection(
    val identifier: UUID,
    val uid: String,
    val name: String,
    val username: String,
    val email: String,
    val image: String,
    val active: Boolean,
    val follows: Set<IFollowsProjection>,
    val episodes: Set<EpisodesProjection>
)

data class UserProjectionTimeWatched(
    val identifier: UUID,
    val uid: String,
    val name: String,
    val username: String,
    val email: String,
    val image: String,
    val active: Boolean,
    val follows: Set<IFollowsProjection>,
    val episodes: Set<EpisodesProjection>,
    val timeWatched: Long
)

interface IFollowsProjection {
    val identifier: UUID
    val username: String
}

data class FollowsProjection(
    override val identifier: UUID,
    override val username: String
) : IFollowsProjection

data class EpisodesProjection(
    val identifier: UUID,
    val duration: Long,
)