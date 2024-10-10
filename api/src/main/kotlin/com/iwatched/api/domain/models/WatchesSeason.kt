package com.iwatched.api.domain.models

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode

@RelationshipProperties
data class WatchesSeason(
    @TargetNode val season: Season,
    @Id
    @GeneratedValue
    val id: Long?
) {
    constructor(season: Season) : this(season, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WatchesSeason) return false

        return season.identifier == other.season.identifier
    }

    override fun hashCode(): Int {
        return season.hashCode()
    }
}