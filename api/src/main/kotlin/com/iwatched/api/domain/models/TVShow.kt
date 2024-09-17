package com.iwatched.api.domain.models

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Relationship
import java.time.LocalDate
import java.util.*

data class TVShow(
    @Id val identifier: UUID = UUID.randomUUID(),
    val title: String,
    val description: String,
    val releaseDate: LocalDate,
    val endDate: LocalDate,
    @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
    var seasons: MutableSet<Season> = mutableSetOf()
)