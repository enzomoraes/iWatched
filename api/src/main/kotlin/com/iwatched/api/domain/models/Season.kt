package com.iwatched.api.domain.models

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Relationship
import java.time.LocalDate
import java.util.*

data class Season(
    @Id val identifier: UUID = UUID.randomUUID(),
    val label: String,
    val releaseDate: LocalDate,
    val thumbnail: String,
    @Relationship(type = "HAS", direction = Relationship.Direction.OUTGOING)
    var episodes: MutableSet<Episode> = mutableSetOf()
)