package com.iwatched.api.domain.repositories

import com.iwatched.api.domain.models.TVShow
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TVShowRepository : Neo4jRepository<TVShow, UUID>