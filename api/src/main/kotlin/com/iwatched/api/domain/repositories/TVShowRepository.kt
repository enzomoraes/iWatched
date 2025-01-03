package com.iwatched.api.domain.repositories

import com.iwatched.api.domain.dto.TVShowFilters
import com.iwatched.api.domain.models.TVShow
import com.iwatched.api.domain.repositories.projections.TVShowProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TVShowRepository : Neo4jRepository<TVShow, UUID>, CustomTVShowRepository {
    @Query(
        "MATCH (s:Season {identifier: \$seasonIdentifier}) " +
                "MATCH (tv:TVShow)-[:HAS]->(s:Season) " +
                "RETURN tv"
    )
    fun findTVShowBySeasonIdentifier(seasonIdentifier: UUID): Optional<TVShow>
}

interface CustomTVShowRepository {
    fun findBy(filters: TVShowFilters, pageable: Pageable): Page<TVShowProjection>
}