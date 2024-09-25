package com.iwatched.api.domain.repositories.projections

import java.util.*

interface IUserProjection {
    val identifier: UUID
    val uid: String
    val name: String
    val username: String
    val email: String
    val image: String
    val active: Boolean
}

interface IUserDetails {
    val identifier: UUID
    val uid: String
    val name: String
    val username: String
    val email: String
    val image: String
    val active: Boolean
    val follows: Set<IFollowsProjection>
    val episodes: Set<IWatchesEpsProjection>
    val timeWatched: Long
}

interface IWatchesEpsProjection {
    val episode: IEpisodeProjection
}

interface IEpisodeProjection {
    val identifier: UUID
    val duration: Long
}

interface IFollowsProjection {
    val identifier: UUID
    val username: String
}

data class UserDetails(
    override val identifier: UUID,
    override val uid: String,
    override val name: String,
    override val username: String,
    override val email: String,
    override val image: String,
    override val active: Boolean,
    override val follows: Set<IFollowsProjection>,
    override val episodes: Set<IWatchesEpsProjection>,
    override val timeWatched: Long
) : IUserDetails

data class UserProjection(
    override val identifier: UUID,
    override val uid: String,
    override val name: String,
    override val username: String,
    override val email: String,
    override val image: String,
    override val active: Boolean,
) : IUserProjection

data class FollowsProjection(
    override val identifier: UUID,
    override val username: String
) : IFollowsProjection

data class WatchesEpsProjection(
    override val episode: EpisodeProjection,
) : IWatchesEpsProjection

data class EpisodeProjection(
    override val identifier: UUID,
    override val duration: Long,
) : IEpisodeProjection