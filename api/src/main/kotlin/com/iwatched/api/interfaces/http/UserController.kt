package com.iwatched.api.interfaces.http

import com.iwatched.api.domain.dto.*
import com.iwatched.api.domain.repositories.projections.IUserProjection
import com.iwatched.api.domain.repositories.projections.UserDetails
import com.iwatched.api.domain.useCases.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping
    fun findAll(
        pageable: Pageable
    ): Page<IUserProjection> = userService.findAllUsers(pageable)

    @GetMapping("/{identifier}")
    fun findByIdentifier(@PathVariable identifier: UUID): Optional<UserDetails> {
        return userService.findByIdentifierWithTimeWatched(identifier)
    }

    @PostMapping
    fun createUser(@RequestBody userCreateDTO: UserCreateDTO): IUserProjection {
        return userService.createUser(userCreateDTO)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody updatedUser: UserUpdateDTO): IUserProjection {
        return userService.updateUser(id, updatedUser)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id: UUID) {
        userService.deleteUser(id)
    }

    @PatchMapping("/{id}/activate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun activateUser(@PathVariable id: UUID) {
        return userService.activateUser(id)
    }

    @PatchMapping("/{id}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deactivateUser(@PathVariable id: UUID) {
        return userService.deactivateUser(id)
    }

    @PostMapping("/follow")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun followUser(@RequestBody followRequestDTO: FollowRequestDTO) {
        userService.followUser(followRequestDTO.followerId, followRequestDTO.followeeId)
    }

    @PostMapping("/watch-tv-show")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun watchTvShow(@RequestBody watchRequest: WatchTVShowDTO) {
        userService.watchTvShow(watchRequest.userId, watchRequest.tvShowId)
    }

    @PostMapping("/watch-season")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun watchSeason(@RequestBody watchRequest: WatchSeasonDTO) {
        userService.watchSeason(watchRequest.userId, watchRequest.seasonId)
    }

    @PostMapping("/watch-episode")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun watchTvShow(@RequestBody watchRequest: WatchEpisodeRequestDTO) {
        userService.watchEpisode(watchRequest.userId, watchRequest.episodeId)
    }

    @PostMapping("/rank-tv-show")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun rankTvShow(@RequestBody rankTvShowRequest: RankTvShowDTO) {
        userService.rankTvShow(rankTvShowRequest.userId, rankTvShowRequest.tvShowId, rankTvShowRequest.rank)
    }

    @PostMapping("/currently-watching-tv-show")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun currentlyWatchingTvShow(@RequestBody rankTvShowRequest: CurrentlyWatchingTvShowDTO) {
        userService.markTvShowAsCurrentlyWatching(rankTvShowRequest.userId, rankTvShowRequest.tvShowId)
    }
}
