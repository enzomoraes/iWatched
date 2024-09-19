package com.iwatched.api.infra

import com.iwatched.api.domain.dto.TVShowFilters
import com.iwatched.api.domain.repositories.CustomTVShowRepository
import com.iwatched.api.domain.repositories.projections.TVShowProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.neo4j.core.Neo4jClient
import org.springframework.stereotype.Component
import java.util.*

@Component
class TVShowRepositoryImpl(
    private val neo4jClient: Neo4jClient
) : CustomTVShowRepository {

    override fun findBy(filters: TVShowFilters, pageable: Pageable): Page<TVShowProjection> {
        val queryBuilder = StringBuilder(
            """
            MATCH (show:TVShow)
        """
        )

        // Adicionar filtros dinamicamente, caso não sejam nulos
        val conditions = mutableListOf<String>()
        filters.title?.let {
            conditions.add("show.title CONTAINS \$title")
        }

        if (conditions.isNotEmpty()) {
            queryBuilder.append(" WHERE ").append(conditions.joinToString(" AND "))
        }

        queryBuilder.append(
            """
            RETURN show
            ORDER BY show.title
            SKIP ${pageable.offset} LIMIT ${pageable.pageSize}
        """
        )

        val query = queryBuilder.toString()

        // Executar a consulta com Neo4jClient
        val result = neo4jClient.query(query)
            .bindAll(
                mapOf(
                    "title" to filters.title
                )
            )
            .fetchAs(TVShowProjection::class.java)
            .mappedBy { _, record ->
                TVShowProjection(
                    identifier = UUID.fromString(record["show"]["identifier"].asString()),
                    title = record["show"]["title"].asString(),
                    description = record["show"]["description"].asString(),
                    releaseDate = record["show"]["releaseDate"].asLocalDate(),
                    endDate = record["show"]["endDate"].asLocalDate()
                )
            }
            .all()

        return PageImpl(result.toList(), pageable, result.size.toLong())
    }
}
