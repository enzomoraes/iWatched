package com.iwatched.api.domain.useCases

import com.iwatched.api.domain.dto.TVShowFilters
import com.iwatched.api.domain.repositories.EpisodeRepository
import com.iwatched.api.domain.repositories.SeasonRepository
import com.iwatched.api.domain.repositories.TVShowRepository
import com.iwatched.api.domain.repositories.projections.TVShowProjection
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

class TVShowServiceTest {

    private lateinit var tvShowService: TVShowService
    private lateinit var episodeRepository: EpisodeRepository
    private lateinit var seasonRepository: SeasonRepository
    private lateinit var tvShowRepository: TVShowRepository
    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @BeforeEach
    fun setUp() {
        tvShowRepository = mock(TVShowRepository::class.java)
        episodeRepository = mock(EpisodeRepository::class.java)
        seasonRepository = mock(SeasonRepository::class.java)
        tvShowService = TVShowService(
            tvShowRepository, episodeRepository, seasonRepository
        )
    }

    @Test
    fun `should return paged list of tv shows`() {
        // Given
        val pageable: Pageable = PageRequest.of(0, 10)
        val tvShowProjections: List<TVShowProjection> = listOf(mock(TVShowProjection::class.java))
        val pagedTVShows: Page<TVShowProjection> =
            PageImpl(tvShowProjections, pageable, tvShowProjections.size.toLong())
        val filters = TVShowFilters("")

        // When
        `when`(tvShowRepository.findBy(filters, pageable)).thenReturn(pagedTVShows)

        val result = tvShowService.findAll(filters, pageable)

        // Then
        assertNotNull(result)
        assertEquals(1, result.content.size)
        verify(tvShowRepository, times(1)).findBy(filters, pageable)
    }

}
