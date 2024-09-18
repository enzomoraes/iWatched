package com.iwatched.api.domain.useCases

import com.iwatched.api.domain.dto.UserCreateDTO
import com.iwatched.api.domain.dto.UserUpdateDTO
import com.iwatched.api.domain.models.User
import com.iwatched.api.domain.repositories.UserRepository
import com.iwatched.api.domain.repositories.projections.UserProjection
import com.iwatched.api.factories.TvShowFactory
import com.iwatched.api.factories.UserFactory
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
    private lateinit var tvShowService: TVShowService
    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @BeforeEach
    fun setUp() {
        tvShowService = mock(TVShowService::class.java)
        userRepository = mock(UserRepository::class.java)
        userService = UserService(userRepository, tvShowService)
    }

    @Test
    fun `should create a new user`() {
        // Given
        val temp = UserFactory.createUser("someone")
        val userCreateDTO = UserCreateDTO(temp.uid, temp.name, temp.email, temp.image, true)
        val savedUser = User(
            uid = userCreateDTO.uid,
            name = userCreateDTO.name,
            email = userCreateDTO.email,
            image = userCreateDTO.image
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
        val existingUser = UserFactory.createUser("someone", userId)
        val userUpdateDTO = UserUpdateDTO(
            name = "someone Updated",
            username = "someone_updated"
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

        val follower = UserFactory.createUser("user1", followerId)
        val followee = UserFactory.createUser("user2", followeeId)

        // When
        `when`(userRepository.findById(followerId)).thenReturn(Optional.of(follower))
        `when`(userRepository.findById(followeeId)).thenReturn(Optional.of(followee))

        userService.followUser(followerId, followeeId)

        // Then
        assertTrue(follower.follows.contains(followee))
        verify(userRepository, times(1)).save(follower)
    }

    @Test
    fun `should watch a whole TVShow`() {
        // Given
        val followerId = UUID.randomUUID()
        val follower = UserFactory.createUser("user1", followerId)

        val tvShow = TvShowFactory.createTVShow()

        // When
        `when`(userRepository.findById(followerId)).thenReturn(Optional.of(follower))
        `when`(tvShowService.findByIdentifier(tvShow.identifier)).thenReturn(Optional.of(tvShow))

        userService.watchTvShow(followerId, tvShow.identifier)

        // Then
        assertTrue(follower.episodes.size == tvShow.seasons.map { s -> s.episodes }.flatten().toMutableSet().size)
        verify(userRepository, times(1)).save(follower)
    }

    @Test
    fun `should watch a whole Season`() {
        // Given
        val followerId = UUID.randomUUID()
        val follower = UserFactory.createUser("user1", followerId)

        val tvShow = TvShowFactory.createTVShow()
        val season = tvShow.seasons.first()

        // When
        `when`(userRepository.findById(followerId)).thenReturn(Optional.of(follower))
        `when`(tvShowService.findSeasonByIdentifier(season.identifier)).thenReturn(Optional.of(season))

        userService.watchSeason(followerId, season.identifier)

        // Then
        assertTrue(follower.episodes.size == season.episodes.toMutableSet().size)
        verify(userRepository, times(1)).save(follower)
    }

    @Test
    fun `should watch an episode`() {
        // Given
        val followerId = UUID.randomUUID()
        val follower = UserFactory.createUser("user1", followerId)

        val tvShow = TvShowFactory.createTVShow()
        val episode = tvShow.seasons.first().episodes.first()

        // When
        `when`(userRepository.findById(followerId)).thenReturn(Optional.of(follower))
        `when`(tvShowService.findEpisodeByIdentifier(episode.identifier)).thenReturn(
            Optional.of(episode)
        )

        userService.watchEpisode(followerId, episode.identifier)

        // Then
        assertTrue(follower.episodes.size == 1)
        verify(userRepository, times(1)).save(follower)
    }
}
