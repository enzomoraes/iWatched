package com.iwatched.api.domain.repositories.projections

import java.time.LocalDate
import java.util.*

data class TVShowProjection(
    val identifier: UUID,
    val title: String,
    val description: String,
    val releaseDate: LocalDate,
    val endDate: LocalDate
)
