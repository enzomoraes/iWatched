package com.iwatched.api.infra

import com.iwatched.api.domain.repositories.projections.UserProjectionTimeWatched
import com.iwatched.api.domain.repositories.projections.UserProjectionTimeWatched.EpisodesProjection
import com.iwatched.api.domain.repositories.projections.UserProjectionTimeWatched.FollowsProjection
import org.springframework.data.neo4j.core.Neo4jClient
import org.springframework.stereotype.Component
import java.util.*

class UserProjectionTimeWatchedImpl(
    override val identifier: UUID,
    override val uid: String,
    override val name: String,
    override val username: String,
    override val email: String,
    override val image: String,
    override val active: Boolean,
    override val follows: Set<FollowsProjection>,
    override val episodes: Set<EpisodesProjection>,
    override val timeWatched: Long,
) : UserProjectionTimeWatched

class EpisodesProjectionImpl(
    override val identifier: UUID,
    override val duration: Long
) : EpisodesProjection

class FollowsProjectionImpl(
    override val identifier: UUID,
    override val username: String
) : FollowsProjection

@Component
class UserRepositoryImpl(
    private val neo4jClient: Neo4jClient
) {
    fun findByIdentifierWithTimeWatched(identifier: UUID): Optional<UserProjectionTimeWatched> {
        val query = """
            MATCH (u:User {identifier: '$identifier'})
            OPTIONAL MATCH (u)-[:WATCHES]->(e:Episode)
            OPTIONAL MATCH (u)-[:FOLLOWS]->(f:User)
            RETURN u, sum(e.duration) as timeWatched, collect(e) as episodes, collect(f) as follows
        """
        return neo4jClient.query(query).bind(identifier.toString()).to("identifier")
            .fetchAs(UserProjectionTimeWatched::class.java)
            .mappedBy { _, record ->
                val episodes = (record["episodes"].asList { episodeRecord ->
                    EpisodesProjectionImpl(
                        identifier = UUID.fromString(episodeRecord["identifier"].asString()),
                        duration = episodeRecord["duration"].asLong()
                    )
                }).toSet()

                val follows = (record["follows"].asList { followRecord ->
                    FollowsProjectionImpl(
                        identifier = UUID.fromString(followRecord["identifier"].asString()),
                        username = followRecord["username"].asString()
                    )
                }).toSet()
                UserProjectionTimeWatchedImpl(
                    identifier = UUID.fromString(record["u"]["identifier"].asString()),
                    uid = record["u"]["uid"].asString(),
                    name = record["u"]["name"].asString(),
                    username = record["u"]["username"].asString(),
                    email = record["u"]["email"].asString(),
                    image = record["u"]["image"].asString(),
                    active = record["u"]["active"].asBoolean(),
                    timeWatched = record["timeWatched"].asLong(),
                    follows = follows,
                    episodes = episodes
                )
            }.one()
    }
}