package com.iwatched.api.domain.useCases

import com.iwatched.api.domain.dto.UserCreateDTO
import com.iwatched.api.domain.dto.UserUpdateDTO
import com.iwatched.api.domain.models.User
import com.iwatched.api.domain.repositories.UserRepository
import com.iwatched.api.domain.repositories.projections.UserProjection
import com.iwatched.api.domain.repositories.projections.UserProjectionTimeWatched
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(private val userRepository: UserRepository, private val tvShowService: TVShowService) {

    fun findAllUsers(pageable: Pageable): Page<UserProjection> = userRepository.findByActive(page = pageable)

    fun findByIdentifier(id: UUID): Optional<UserProjection> = userRepository.findByIdentifier(id)

    fun findByIdentifierWithTimeWatched(id: UUID): Optional<UserProjectionTimeWatched> =
        userRepository.findByIdentifierWithTimeWatched(id)

    fun createUser(userCreateDTO: UserCreateDTO): UserProjection {
        val createdUser = userRepository.save(
            User(
                uid = userCreateDTO.uid,
                name = userCreateDTO.name,
                email = userCreateDTO.email,
                image = userCreateDTO.image,
                active = userCreateDTO.isActive
            )
        )
        return findByIdentifier(createdUser.identifier).orElseThrow { RuntimeException("User not found") }
    }

    fun updateUser(id: UUID, updatedUser: UserUpdateDTO): UserProjection {
        val existingUser = userRepository.findById(id).orElseThrow { RuntimeException("User not found") }
        val userToUpdate = existingUser.copy(
            name = updatedUser.name,
            username = updatedUser.username
        )
        userRepository.save(userToUpdate)
        return findByIdentifier(existingUser.identifier).orElseThrow { RuntimeException("User not found") }
    }

    fun deleteUser(id: UUID) {
        userRepository.deleteById(id)
    }

    fun activateUser(id: UUID) {
        val user = userRepository.findById(id).orElseThrow { RuntimeException("User not found") }
        user.active = true
        userRepository.save(user)
    }

    fun deactivateUser(id: UUID) {
        val user = userRepository.findById(id).orElseThrow { RuntimeException("User not found") }
        user.active = false
        userRepository.save(user)
    }

    fun followUser(followerId: UUID, followeeId: UUID) {
        val follower = userRepository.findById(followerId).orElseThrow { RuntimeException("Follower not found") }
        val followee = userRepository.findById(followeeId).orElseThrow { RuntimeException("Followee not found") }

        follower.follow(followee)

        userRepository.save(follower)
    }

    fun watchTvShow(userId: UUID, tvShowId: UUID) {
        val watcher = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
        val tvShow = tvShowService.findByIdentifier(tvShowId).orElseThrow { RuntimeException("TV Show not found") }
        watcher.watchTVShow(tvShow)

        userRepository.save(watcher)
    }

    fun watchSeason(userId: UUID, seasonId: UUID) {
        val watcher = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
        val season =
            tvShowService.findSeasonByIdentifier(seasonId).orElseThrow { RuntimeException("TV Show not found") }
        watcher.watchSeason(season)

        userRepository.save(watcher)
    }

    fun watchEpisode(userId: UUID, episodeId: UUID) {
        val watcher = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
        val episode =
            tvShowService.findEpisodeByIdentifier(episodeId).orElseThrow { RuntimeException("Episode not found") }
        watcher.watchEpisode(episode)

        userRepository.save(watcher)
    }
}
