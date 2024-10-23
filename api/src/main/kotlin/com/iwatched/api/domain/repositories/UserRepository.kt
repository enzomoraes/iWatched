package com.iwatched.api.domain.repositories

import com.iwatched.api.domain.models.User
import com.iwatched.api.domain.repositories.projections.IUserProjection
import com.iwatched.api.domain.repositories.projections.UserDetails
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.neo4j.repository.Neo4jRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : Neo4jRepository<User, UUID>, CustomUserRepository {
    fun findByActive(active: Boolean = true, page: Pageable): Page<IUserProjection>
    fun findByIdentifier(identifier: UUID): Optional<IUserProjection>
}

interface CustomUserRepository {
    fun findByIdentifierWithTimeWatched(identifier: UUID): Optional<UserDetails>
}