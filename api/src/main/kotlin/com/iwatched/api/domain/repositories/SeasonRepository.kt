package com.iwatched.api.domain.repositories

import com.iwatched.api.domain.models.Season
import com.iwatched.api.domain.models.TVShow
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SeasonRepository : Neo4jRepository<Season, UUID> {
    @Query(
        "MATCH (ep:Episode {identifier: \$episodeIdentifier})\n" +
        "MATCH (s:Season)-[:HAS]->(ep:Episode)\n" +
        "RETURN s"
    )
    fun findSeasonByEpisodeIdentifier(episodeIdentifier: UUID): Optional<Season>
}