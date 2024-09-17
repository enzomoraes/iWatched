package com.iwatched.api.domain.models

import org.springframework.data.neo4j.core.schema.Id
import java.util.*

data class Episode(
    @Id val identifier: UUID = UUID.randomUUID(),
    val number: Int,
    val title: String,
    val duration: Long,
    val description: String,
    val thumbnail: String,
)