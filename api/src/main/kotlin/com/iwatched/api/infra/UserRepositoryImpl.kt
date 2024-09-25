package com.iwatched.api.infra

import com.iwatched.api.domain.repositories.CustomUserRepository
import com.iwatched.api.domain.repositories.projections.EpisodeProjection
import com.iwatched.api.domain.repositories.projections.WatchesEpsProjection
import com.iwatched.api.domain.repositories.projections.FollowsProjection
import com.iwatched.api.domain.repositories.projections.UserDetails
import org.springframework.data.neo4j.core.Neo4jClient
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserRepositoryImpl(
    private val neo4jClient: Neo4jClient
) : CustomUserRepository {
    override fun findByIdentifierWithTimeWatched(identifier: UUID): Optional<UserDetails> {
        val query = """
            MATCH (u:User {identifier: '$identifier'})
            OPTIONAL MATCH (u)-[WATCHES_EP]->(e:Episode)
            OPTIONAL MATCH (u)-[:FOLLOWS]->(f:User)
            RETURN u, sum(e.duration) as timeWatched, collect(e) as episodes, collect(f) as follows
        """
        return neo4jClient.query(query).bind(identifier.toString()).to("identifier")
            .fetchAs(UserDetails::class.java)
            .mappedBy { _, record ->
//                val watchesProperties = record["watchesProperties"].asList { it.asMap() }
                val episodes = record["episodes"].asList { it.asMap() }

                val mergedEpisodes = episodes.mapIndexed { index, episodeRecord ->
//                    val watchProps = watchesProperties[index]
                    WatchesEpsProjection(
//                        currentlyWatching = watchProps["currentlyWatching"] as Boolean,
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
                    episodes = mergedEpisodes
                )
            }.one()
    }
}