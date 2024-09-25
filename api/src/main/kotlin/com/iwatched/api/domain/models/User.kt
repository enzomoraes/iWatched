package com.iwatched.api.domain.models

import org.springframework.data.neo4j.core.schema.Id
import org.springframework.data.neo4j.core.schema.Relationship
import java.util.*

data class User(
    @Id val identifier: UUID = UUID.randomUUID(),
    val uid: String,
    val name: String,
    val username: String? = name,
    val email: String,
    val image: String,
    var active: Boolean? = true,
    @Relationship(type = "FOLLOWS", direction = Relationship.Direction.OUTGOING)
    var follows: MutableSet<User> = mutableSetOf(),
    @Relationship(type = "WATCHES_EP", direction = Relationship.Direction.OUTGOING)
    var episodes: MutableSet<WatchesEp> = mutableSetOf(),
) {

    fun follow(user: User): User {
        this.follows.add(user)
        return this
    }

    fun watchTVShow(tvShow: TVShow): User {
        for (seasons in tvShow.seasons) {
            for (ep in seasons.episodes) {
                this.episodes.add(WatchesEp(ep))
            }
        }
        return this
    }

    fun watchSeason(season: Season): User {
        for (ep in season.episodes) {
            this.episodes.add(WatchesEp(ep))
        }
        return this
    }

    fun watchEpisode(episode: Episode): User {
        this.episodes.add(WatchesEp(episode))
        return this
    }

}