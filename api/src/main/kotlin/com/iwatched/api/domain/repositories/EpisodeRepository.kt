package com.iwatched.api.domain.repositories

import com.iwatched.api.domain.models.Episode
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EpisodeRepository : Neo4jRepository<Episode, UUID>