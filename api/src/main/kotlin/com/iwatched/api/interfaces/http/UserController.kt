package com.iwatched.api.interfaces.http

import com.iwatched.api.domain.dto.FollowRequestDTO
import com.iwatched.api.domain.dto.UserCreateDTO
import com.iwatched.api.domain.dto.UserUpdateDTO
import com.iwatched.api.domain.models.User
import com.iwatched.api.domain.repositories.projections.UserProjection
import com.iwatched.api.domain.useCases.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @GetMapping
    fun findAll(
        pageable: Pageable
    ): Page<UserProjection> = userService.findAllUsers(pageable)

    @GetMapping("/{id}")
    fun findByIdentifier(@PathVariable identifier: UUID): Optional<UserProjection> {
        return userService.findByIdentifier(identifier)
    }

    @PostMapping
    fun createUser(@RequestBody userCreateDTO: UserCreateDTO): UserProjection {
        return userService.createUser(userCreateDTO)
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: UUID, @RequestBody updatedUser: UserUpdateDTO): UserProjection {
        return userService.updateUser(id, updatedUser)
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: UUID) {
        userService.deleteUser(id)
    }

    @PatchMapping("/{id}/activate")
    fun activateUser(@PathVariable id: UUID) {
        return userService.activateUser(id)
    }

    @PatchMapping("/{id}/deactivate")
    fun deactivateUser(@PathVariable id: UUID) {
        return userService.deactivateUser(id)
    }

    @PostMapping("/follow")
    fun followUser(@RequestBody followRequestDTO: FollowRequestDTO) {
        userService.followUser(followRequestDTO.followerId, followRequestDTO.followeeId)
    }
}