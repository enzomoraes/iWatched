package com.iwatched.api.domain.useCases

import com.iwatched.api.domain.dto.UserCreateDTO
import com.iwatched.api.domain.dto.UserUpdateDTO
import com.iwatched.api.domain.exceptions.EntityNotFound
import com.iwatched.api.domain.models.Episode
import com.iwatched.api.domain.models.Season
import com.iwatched.api.domain.models.TVShow
import com.iwatched.api.domain.models.User
import com.iwatched.api.domain.repositories.UserRepository
import com.iwatched.api.domain.repositories.projections.IUserProjection
import com.iwatched.api.domain.repositories.projections.UserDetails
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class UserService(private val userRepository: UserRepository, private val tvShowService: TVShowService) {

    fun findAllUsers(pageable: Pageable): Page<IUserProjection> = userRepository.findByActive(page = pageable)

    fun findByIdentifier(id: UUID): Optional<IUserProjection> = userRepository.findByIdentifier(id)

    fun findByIdentifierWithTimeWatched(id: UUID): Optional<UserDetails> =
        userRepository.findByIdentifierWithTimeWatched(id)

    fun createUser(userCreateDTO: UserCreateDTO): IUserProjection {
        val createdUser = userRepository.save(
            User(
                uid = userCreateDTO.uid,
                name = userCreateDTO.name,
                email = userCreateDTO.email,
                image = userCreateDTO.image,
                active = userCreateDTO.isActive
            )
        )
        return findByIdentifier(createdUser.identifier).orElseThrow { EntityNotFound("User not found", User::class) }
    }

    fun updateUser(id: UUID, updatedUser: UserUpdateDTO): IUserProjection {
        val existingUser = userRepository.findById(id).orElseThrow { EntityNotFound("User not found", User::class) }
        val userToUpdate = existingUser.copy(
            name = updatedUser.name,
            username = updatedUser.username
        )
        userRepository.save(userToUpdate)
        return findByIdentifier(existingUser.identifier).orElseThrow { EntityNotFound("User not found", User::class) }
    }

    fun deleteUser(id: UUID) {
        userRepository.deleteById(id)
    }

    fun activateUser(id: UUID) {
        val user = userRepository.findById(id).orElseThrow { EntityNotFound("User not found", User::class) }
        user.active = true
        userRepository.save(user)
    }

    fun deactivateUser(id: UUID) {
        val user = userRepository.findById(id).orElseThrow { EntityNotFound("User not found", User::class) }
        user.active = false
        userRepository.save(user)
    }

    fun followUser(followerId: UUID, followeeId: UUID) {
        val follower = userRepository.findById(followerId).orElseThrow { EntityNotFound("Follower not found", User::class) }
        val followee = userRepository.findById(followeeId).orElseThrow { EntityNotFound("Followee not found", User::class) }

        follower.follow(followee)

        userRepository.save(follower)
    }

    fun watchTvShow(userId: UUID, tvShowId: UUID) {
        val watcher = userRepository.findById(userId).orElseThrow { EntityNotFound("User not found", User::class) }
        val tvShow = tvShowService.findByIdentifier(tvShowId).orElseThrow { EntityNotFound("TV Show not found", TVShow::class) }
        watcher.watchTVShow(tvShow)

        userRepository.save(watcher)
    }

    fun watchSeason(userId: UUID, seasonId: UUID) {
        val watcher = userRepository.findById(userId).orElseThrow { EntityNotFound("User not found", User::class) }
        val season = tvShowService.findSeasonByIdentifier(seasonId).orElseThrow { EntityNotFound("Season not found", Season::class) }

        watcher.watchSeason(season)

        checkUserHasWatchedAllSeasons(seasonId, watcher)

        userRepository.save(watcher)
    }

    fun watchEpisode(userId: UUID, episodeId: UUID) {
        val watcher = userRepository.findById(userId).orElseThrow { EntityNotFound("User not found", User::class) }
        val episode =
            tvShowService.findEpisodeByIdentifier(episodeId).orElseThrow { EntityNotFound("Episode not found", Episode::class) }

        watcher.watchEpisode(episode)

        checkUserHasWatchedAllEpisodes(episodeId, watcher)

        userRepository.save(watcher)
    }

    private fun checkUserHasWatchedAllEpisodes(episodeId: UUID, watcher: User) {
        val season =
            userRepository.getWatchedSeasonIfAllEpisodesWatched(watcher.identifier, episodeId).getOrElse { return }

        watcher.watchSeason(season)
        checkUserHasWatchedAllSeasons(season.identifier, watcher)
    }

    private fun checkUserHasWatchedAllSeasons(seasonId: UUID, watcher: User) {
        val tvShow =
            userRepository.getWatchedTvShowIfAllSeasonsWatched(watcher.identifier, seasonId).getOrElse { return }

        watcher.watchTVShow(tvShow)
    }

}
