package com.iwatched.api.domain.repositories

import com.iwatched.api.domain.models.Season
import com.iwatched.api.domain.models.TVShow
import com.iwatched.api.domain.models.User
import com.iwatched.api.domain.repositories.projections.IUserProjection
import com.iwatched.api.domain.repositories.projections.UserDetails
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : Neo4jRepository<User, UUID>, CustomUserRepository {
    fun findByActive(active: Boolean = true, page: Pageable): Page<IUserProjection>
    fun findByIdentifier(identifier: UUID): Optional<IUserProjection>

    @Query(
        "    MATCH (u:User {identifier: '\$userId'})\n" +
                "    MATCH (e:Episode {identifier: '\$episodeId'})<-[:HAS]-(s:Season)\n" +
                "    MATCH (s)-[:HAS]->(allEpisodes:Episode)\n" +
                "    WHERE (u)-[:WATCHES_EP]->(allEpisodes)\n" +
                "    RETURN s"
    )
    fun getWatchedSeasonIfAllEpisodesWatched(@Param("userId") userId: UUID, @Param("episodeId") episodeId: UUID): Optional<Season>

    @Query(
        "    MATCH (u:User {identifier: '\$userId'})\n" +
                "    MATCH (s:Season {identifier: '\$seasonId'})<-[:HAS]-(tv:TVShow)\n" +
                "    MATCH (tv)-[:HAS]->(allSeasons:Season)\n" +
                "    WHERE (u)-[:WATCHES_SEASON]->(allSeasons)\n" +
                "    RETURN tv"
    )
    fun getWatchedTvShowIfAllSeasonsWatched(@Param("userId") userId: UUID, @Param("seasonId") seasonId: UUID): Optional<TVShow>
}

interface CustomUserRepository {
    fun findByIdentifierWithTimeWatched(identifier: UUID): Optional<UserDetails>
}