package com.iwatched.api.domain.repositories

import com.iwatched.api.domain.models.User
import com.iwatched.api.domain.repositories.projections.UserProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : Neo4jRepository<User, UUID> {
    fun findByActive(page: Pageable, active: Boolean = true): Page<UserProjection>
    fun findByIdentifier(identifier: UUID): Optional<UserProjection>
}
