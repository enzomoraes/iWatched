package com.iwatched.api.domain.useCases

import com.iwatched.api.domain.dto.UserCreateDTO
import com.iwatched.api.domain.dto.UserUpdateDTO
import com.iwatched.api.domain.models.User
import com.iwatched.api.domain.repositories.UserRepository
import com.iwatched.api.domain.repositories.projections.UserProjection
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.util.*

class UserServiceTest {

    private lateinit var userService: UserService
    private lateinit var userRepository: UserRepository
    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        userService = UserService(userRepository)
    }

    @Test
    fun `should create a new user`() {
        // Given
        val userCreateDTO = UserCreateDTO(
            uid = "user1",
            name = "Enzo Moraes",
            email = "enzo@example.com",
            image = "profile.jpg",
            isActive = true
        )
        val savedUser = User(
            identifier = UUID.randomUUID(),
            uid = userCreateDTO.uid,
            name = userCreateDTO.name,
            email = userCreateDTO.email,
            image = userCreateDTO.image,
            active = userCreateDTO.isActive
        )

        // When
        `when`(userRepository.save(any(User::class.java))).thenReturn(savedUser)

        `when`(userRepository.findByIdentifier(any(UUID::class.java)))
            .thenReturn(Optional.of(mock(UserProjection::class.java)))

        val result = userService.createUser(userCreateDTO)

        // Then
        assertNotNull(result)
        verify(userRepository, times(1)).save(any(User::class.java))
    }

    @Test
    fun `should return paged list of users`() {
        // Given
        val pageable: Pageable = PageRequest.of(0, 10)
        val userProjections: List<UserProjection> = listOf(mock(UserProjection::class.java))
        val pagedUsers: Page<UserProjection> = PageImpl(userProjections, pageable, userProjections.size.toLong())

        // When
        `when`(userRepository.findByActive(pageable)).thenReturn(pagedUsers)

        val result = userService.findAllUsers(pageable)

        // Then
        assertNotNull(result)
        assertEquals(1, result.content.size)
        verify(userRepository, times(1)).findByActive(pageable)
    }

    @Test
    fun `should update user details`() {
        // Given
        val userId = UUID.randomUUID()
        val existingUser = User(
            identifier = userId,
            uid = "user1",
            name = "Enzo Moraes",
            email = "enzo@example.com",
            image = "profile.jpg",
            active = true
        )
        val userUpdateDTO = UserUpdateDTO(
            name = "Enzo Updated",
            username = "enzo_updated"
        )

        // When
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(existingUser))
        `when`(userRepository.findByIdentifier(userId)).thenReturn(Optional.of(mock(UserProjection::class.java)))
        val userCaptor = ArgumentCaptor.forClass(User::class.java)

        val result = userService.updateUser(userId, userUpdateDTO)

        // Then
        assertNotNull(result)
        verify(userRepository).save(userCaptor.capture())
        verify(userRepository, times(1)).save(any(User::class.java))

        val savedUser = userCaptor.value
        assertEquals(userId, savedUser.identifier)
        assertEquals(userUpdateDTO.name, savedUser.name)
        assertEquals(userUpdateDTO.username, savedUser.username)
        // You can also check other fields to ensure they are unchanged
        assertEquals(existingUser.email, savedUser.email)
        assertEquals(existingUser.image, savedUser.image)
        assertEquals(existingUser.active, savedUser.active)
    }

    @Test
    fun `should delete user by id`() {
        // Given
        val userId = UUID.randomUUID()

        // When
        doNothing().`when`(userRepository).deleteById(userId)

        userService.deleteUser(userId)

        // Then
        verify(userRepository, times(1)).deleteById(userId)
    }

    @Test
    fun `should follow another user`() {
        // Given
        val followerId = UUID.randomUUID()
        val followeeId = UUID.randomUUID()

        val follower = User(
            identifier = followerId,
            uid = "follower",
            name = "Follower",
            email = "follower@example.com",
            image = "follower.jpg",
            active = true
        )
        val followee = User(
            identifier = followeeId,
            uid = "followee",
            name = "Followee",
            email = "followee@example.com",
            image = "followee.jpg",
            active = true
        )

        // When
        `when`(userRepository.findById(followerId)).thenReturn(Optional.of(follower))
        `when`(userRepository.findById(followeeId)).thenReturn(Optional.of(followee))

        userService.followUser(followerId, followeeId)

        // Then
        assertTrue(follower.follows.contains(followee))
        verify(userRepository, times(1)).save(follower)
    }
}
