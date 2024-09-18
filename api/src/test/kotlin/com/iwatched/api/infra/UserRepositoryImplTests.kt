package com.iwatched.api.infra

import com.iwatched.api.DatabaseSeeder
import com.iwatched.api.domain.repositories.TVShowRepository
import com.iwatched.api.domain.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserRepositoryImplTests @Autowired constructor(
    private val userRepository: UserRepository,
    private val tvShowRepository: TVShowRepository,
    private val databaseSeeder: DatabaseSeeder
) {

    @BeforeEach
    fun setup() {
        databaseSeeder.seedUser()  // Seed database before each test
        databaseSeeder.seedTVShow()

        userRepository.save(
            userRepository.findById(DatabaseSeeder.USER1_ID).get().watchTVShow(tvShowRepository.findAll().first())
        )
    }

    @Test
    fun `should return user projection with timeWatched not empty`() {
        val user = userRepository.findByIdentifierWithTimeWatched(DatabaseSeeder.USER1_ID).orElseThrow()
        assertNotNull(user.timeWatched)
        assertNotEquals(0, user.episodes.size)
        assertEquals(0, user.follows.size)

        val user2 = userRepository.findByIdentifierWithTimeWatched(DatabaseSeeder.USER2_ID).orElseThrow()
        assertNotNull(user2.timeWatched)
        assertEquals(0, user2.episodes.size)
        assertEquals(1, user2.follows.size)
    }
}