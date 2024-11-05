package com.iwatched.api.infra

import com.iwatched.api.domain.models.Episode
import com.iwatched.api.domain.models.Season
import com.iwatched.api.domain.models.TVShow
import com.iwatched.api.domain.models.WatchesTVShow
import com.iwatched.api.domain.repositories.CustomUserRepository
import com.iwatched.api.domain.repositories.projections.*
import org.springframework.data.neo4j.core.Neo4jClient
import org.springframework.data.neo4j.core.mappedBy
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserRepositoryImpl(
    private val neo4jClient: Neo4jClient
) : CustomUserRepository {
    override fun findByIdentifierWithTimeWatched(identifier: UUID): Optional<UserDetails> {

        val tvShowsQuery = """
            MATCH (u:User {identifier: '$identifier'})-[w:WATCHES_TV_SHOW]->(t:TVShow)
            RETURN collect({tvShow: t, rank: w.rank, currentlyWatching: w.currentlyWatching}) as tvShows
        """

        val tvShows = neo4jClient.query(tvShowsQuery).bind(identifier.toString()).to("identifier")
            .mappedBy { _, record ->
                record["tvShows"].asList { tvShowRecord ->
                    WatchesTvShowProjection(
                        TvShowProjection(
                            identifier = UUID.fromString(tvShowRecord["tvShow"]["identifier"].asString()),
                            title = tvShowRecord["tvShow"]["title"].asString(),
                            rank = tvShowRecord["rank"].asInt(),
                            currentlyWatching = tvShowRecord["currentlyWatching"].asBoolean()
                        )
                    )
                }.toSet()
            }.first()

        val query = """
            MATCH (u:User {identifier: '$identifier'})
            OPTIONAL MATCH (u)-[:WATCHES_EP]->(e:Episode)
            OPTIONAL MATCH (u)-[:FOLLOWS]->(f:User)
            RETURN u, sum(e.duration) as timeWatched, collect(e) as episodes, collect(f) as follows
        """
        return neo4jClient.query(query).bind(identifier.toString()).to("identifier")
            .fetchAs(UserDetails::class.java)
            .mappedBy { _, record ->
                val episodes = record["episodes"].asList { it.asMap() }

                val mergedEpisodes = episodes.map { episodeRecord ->
                    WatchesEpsProjection(
                        episode = EpisodeProjection(
                            identifier = UUID.fromString(episodeRecord["identifier"] as String),
                            duration = episodeRecord["duration"] as Long
                        )
                    )
                }.toSet()

                val follows = record["follows"].asList { followRecord ->
                    FollowsProjection(
                        identifier = UUID.fromString(followRecord["identifier"].asString()),
                        username = followRecord["username"].asString()
                    )
                }.toSet()

                UserDetails(
                    identifier = UUID.fromString(record["u"]["identifier"].asString()),
                    uid = record["u"]["uid"].asString(),
                    name = record["u"]["name"].asString(),
                    username = record["u"]["username"].asString(),
                    email = record["u"]["email"].asString(),
                    image = record["u"]["image"].asString(),
                    active = record["u"]["active"].asBoolean(),
                    timeWatched = record["timeWatched"].asLong(),
                    follows = follows,
                    episodes = mergedEpisodes,
                    tvShows = tvShows
                )
            }.one()
    }
}