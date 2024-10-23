package com.iwatched.api.domain.useCases

import com.iwatched.api.domain.dto.TVShowFilters
import com.iwatched.api.domain.models.Episode
import com.iwatched.api.domain.models.Season
import com.iwatched.api.domain.models.TVShow
import com.iwatched.api.domain.repositories.EpisodeRepository
import com.iwatched.api.domain.repositories.SeasonRepository
import com.iwatched.api.domain.repositories.TVShowRepository
import com.iwatched.api.domain.repositories.projections.TVShowProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*
import javax.swing.text.html.Option


@Service
class TVShowService(
    private val tvShowRepository: TVShowRepository,
    private val episodeRepository: EpisodeRepository,
    private val seasonRepository: SeasonRepository
) {

    fun findAll(filters: TVShowFilters, pageable: Pageable): Page<TVShowProjection> {
        val result = tvShowRepository.findBy(filters, pageable)
        return result
    }

    fun findByIdentifier(identifier: UUID): Optional<TVShow> {
        return tvShowRepository.findById(identifier)
    }

    fun findEpisodeByIdentifier(identifier: UUID): Optional<Episode> {
        return episodeRepository.findById(identifier)
    }

    fun findSeasonByIdentifier(identifier: UUID): Optional<Season> {
        return seasonRepository.findById(identifier)
    }

    fun findSeasonByEpisodeIdentifier(identifier: UUID): Optional<Season> {
        return seasonRepository.findSeasonByEpisodeIdentifier(identifier)
    }

    fun findTVShowBySeasonIdentifier(identifier: UUID): Optional<TVShow> {
        return tvShowRepository.findTVShowBySeasonIdentifier(identifier)
    }

}