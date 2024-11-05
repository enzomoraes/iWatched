package com.iwatched.api.domain.models

import org.springframework.data.neo4j.core.schema.GeneratedValue
import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.RelationshipProperties
import org.springframework.data.neo4j.core.schema.TargetNode

@RelationshipProperties
data class WatchesTVShow(
    @TargetNode val tvShow: TVShow,
    @Id
    @GeneratedValue
    val id: Long?,
    var rank: Int?,
    var currentlyWatching: Boolean = false
) {
    constructor(tvShow: TVShow) : this(tvShow, null, 0, currentlyWatching = false)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WatchesTVShow) return false

        return tvShow.identifier == other.tvShow.identifier
    }

    override fun hashCode(): Int {
        return tvShow.hashCode()
    }
}