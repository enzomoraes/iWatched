package com.iwatched.api.infra

import com.iwatched.api.DatabaseSeeder
import com.iwatched.api.domain.dto.TVShowFilters
import com.iwatched.api.domain.repositories.TVShowRepository
import com.iwatched.api.domain.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TVShowRepositoryImplTests @Autowired constructor(
    private val tvShowRepository: TVShowRepository,
    private val databaseSeeder: DatabaseSeeder
) {

    @BeforeEach
    fun setup() {
        databaseSeeder.resetDatabase()
        databaseSeeder.seedTVShow()
    }

    @Test
    fun `should return tv shows projection`() {
        val tvShows = tvShowRepository.findBy(TVShowFilters(title = null), PageRequest.of(0, 10))
        var count = 0L
        tvShows.content.forEach { _ -> count++}
        assertNotNull(tvShows.content)
        assertEquals(tvShowRepository.count(), count)
    }

    @Test
    fun `should return no tv shows projection`() {
        val tvShows = tvShowRepository.findBy(TVShowFilters(title = "non existent"), PageRequest.of(0, 10))
        var count = 0L
        tvShows.content.forEach { _ -> count++}
        assertNotNull(tvShows.content)
        assertEquals(0, count)
    }
}