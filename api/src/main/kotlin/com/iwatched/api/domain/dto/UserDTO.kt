package com.iwatched.api.domain.dto

import java.util.*

data class UserCreateDTO(
    val uid: String,
    val name: String,
    val email: String,
    val image: String,
    val isActive: Boolean = true
)

data class UserUpdateDTO(
    val name: String,
    val username: String
)

data class FollowRequestDTO(
    val followerId: UUID,
    val followeeId: UUID
)

data class WatchEpisodeRequestDTO(
    val userId: UUID,
    val episodeId: UUID
)

data class WatchTVShowDTO(
    val userId: UUID,
    val tvShowId: UUID
)

data class WatchSeasonDTO(
    val userId: UUID,
    val seasonId: UUID
)

data class RankTvShowDTO(
    val userId: UUID,
    val tvShowId: UUID,
    val rank: Int
)

data class CurrentlyWatchingTvShowDTO(val userId: UUID, val tvShowId: UUID)