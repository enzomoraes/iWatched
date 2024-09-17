package com.iwatched.api.domain.useCases

import com.iwatched.api.domain.models.Episode
import com.iwatched.api.domain.models.Season
import com.iwatched.api.domain.models.TVShow
import com.iwatched.api.domain.repositories.EpisodeRepository
import com.iwatched.api.domain.repositories.SeasonRepository
import com.iwatched.api.domain.repositories.TVShowRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class TVShowService(
    private val tvShowRepository: TVShowRepository,
    private val episodeRepository: EpisodeRepository,
    private val seasonRepository: SeasonRepository
) {

    fun findByIdentifier(identifier: UUID): Optional<TVShow> {
        return tvShowRepository.findById(identifier)
    }

    fun findEpisodeByIdentifier(identifier: UUID): Optional<Episode> {
        return episodeRepository.findById(identifier)
    }

    fun findSeasonByIdentifier(identifier: UUID): Optional<Season> {
        return seasonRepository.findById(identifier)
    }

}