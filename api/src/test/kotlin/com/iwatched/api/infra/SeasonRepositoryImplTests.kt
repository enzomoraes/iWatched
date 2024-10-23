package com.iwatched.api.infra

import com.iwatched.api.DatabaseSeeder
import com.iwatched.api.domain.dto.TVShowFilters
import com.iwatched.api.domain.repositories.SeasonRepository
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
class SeasonRepositoryImplTests @Autowired constructor(
    private val tvShowRepository: TVShowRepository,
    private val seasonRepository: SeasonRepository,
    private val databaseSeeder: DatabaseSeeder
) {

    @BeforeEach
    fun setup() {
        databaseSeeder.resetDatabase()
        databaseSeeder.seedTVShow()
    }

    @Test
    fun `should find season by episode identifier`() {
        val targetSeason = tvShowRepository.findAll().first().seasons.first()
        val season = seasonRepository.findSeasonByEpisodeIdentifier(targetSeason.episodes.first().identifier)

        assertEquals(targetSeason.identifier, season.get().identifier)
    }
}