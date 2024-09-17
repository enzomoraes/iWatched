package com.iwatched.api.domain.repositories

import com.iwatched.api.domain.models.Season
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SeasonRepository : Neo4jRepository<Season, UUID>