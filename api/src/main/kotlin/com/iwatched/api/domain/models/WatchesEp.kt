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
}