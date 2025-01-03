package com.iwatched.api.domain.models

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode

@RelationshipProperties
data class WatchesEp(
    @TargetNode val episode: Episode,
    @Id
    @GeneratedValue
    val id: Long?
) {
    constructor(episode: Episode) : this(episode, null)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WatchesEp) return false

        return episode.identifier == other.episode.identifier
    }

    override fun hashCode(): Int {
        return episode.hashCode()
    }
}