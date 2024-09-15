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
