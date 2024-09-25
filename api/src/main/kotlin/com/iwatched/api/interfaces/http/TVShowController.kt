package com.iwatched.api.interfaces.http

import com.iwatched.api.domain.dto.*
import com.iwatched.api.domain.repositories.projections.TVShowProjection
import com.iwatched.api.domain.useCases.TVShowService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/tv-shows")
class TVShowController(private val tvShowService: TVShowService) {

    @GetMapping
    fun findAll(
        @ModelAttribute filters: TVShowFilters,
        pageable: Pageable
    ): Page<TVShowProjection> = tvShowService.findAll(filters, pageable)

//    @GetMapping("/{identifier}")
//    fun findByIdentifier(@PathVariable identifier: UUID): Optional<TVShowProjection> {
//        return tvShowService.findByIdentifierWithTimeWatched(identifier)
//    }
}
