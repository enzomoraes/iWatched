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
    @Relationship(type = "WATCHES_TV_SHOW", direction = Relationship.Direction.OUTGOING)
    var tvShows: MutableSet<WatchesTVShow> = mutableSetOf(),
    @Relationship(type = "WATCHES_SEASON", direction = Relationship.Direction.OUTGOING)
    var seasons: MutableSet<WatchesSeason> = mutableSetOf(),
) {

    fun follow(user: User): User {
        this.follows.add(user)
        return this
    }

    fun watchTVShow(tvShow: TVShow): User {
        for (season in tvShow.seasons) {
            watchSeason(season)
        }
        if (!this.tvShows.contains(WatchesTVShow(tvShow)))
            this.tvShows.add(WatchesTVShow(tvShow))
        return this
    }

    fun watchSeason(season: Season): User {
        for (ep in season.episodes) {
            this.watchEpisode(ep)
        }
        if (!this.seasons.contains(WatchesSeason(season)))
            this.seasons.add(WatchesSeason(season))
        return this
    }

    fun watchEpisode(episode: Episode): User {
        if (!this.episodes.contains(WatchesEp(episode)))
            this.episodes.add(WatchesEp(episode))
        return this
    }

}