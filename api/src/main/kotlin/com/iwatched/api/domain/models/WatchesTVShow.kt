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
    val id: Long?
) {
    constructor(tvShow: TVShow) : this(tvShow, null)
}