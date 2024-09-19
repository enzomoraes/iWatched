package com.iwatched.api

import com.iwatched.api.factories.TvShowFactory
import com.iwatched.api.factories.UserFactory
import org.neo4j.driver.Driver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Profile("test")
class DatabaseSeeder @Autowired constructor(private val driver: Driver) {

    companion object UserIdentifiers {
        val USER1_ID: UUID = UUID.fromString("fa4fb627-d0d1-484e-9c8c-a08d674d4f68")
        val USER2_ID: UUID = UUID.fromString("13a0d0ef-0ba6-4d53-ab56-27c8880c19b6")
    }

    fun seedUser() {
        println("seeding database")
        val user1 = UserFactory.createUser("John Doe", USER1_ID)
        val user2 = UserFactory.createUser("Elon Musk", USER2_ID)
        driver.session().use { session ->
            session.executeWrite { tx ->
                tx.run("CREATE (u1:User {identifier: '${user1.identifier}', uid: '${user1.uid}', name: '${user1.name}', email: '${user1.email}', image: '${user1.image}', active: ${user1.active}})")
                tx.run("CREATE (u1:User {identifier: '${user2.identifier}', uid: '${user2.uid}', name: '${user2.name}', email: '${user2.email}', image: '${user2.image}', active: ${user2.active}})")
                tx.run("MATCH (u1:User {identifier: '${user1.identifier}'}), (u2:User {identifier: '${user2.identifier}'}) CREATE (u2)-[:FOLLOWS]->(u1)")

                println("Seeding database is finished")
                null
            }
        }
    }

    fun seedTVShow() {
        val tvShow = TvShowFactory.createTVShow()
        driver.session().use { session ->
            session.executeWrite { tx ->
                tx.run(
                    """
                        CREATE (e:TVShow {
                            identifier: $1,
                            title: $2,
                            description: $3,
                            releaseDate: $4,
                            endDate: $5
                        })
                        """.trimIndent(), mapOf(
                        "1" to tvShow.identifier.toString(),
                        "2" to tvShow.title,
                        "3" to tvShow.description,
                        "4" to tvShow.releaseDate,
                        "5" to tvShow.endDate,
                    )
                )
                for (s in tvShow.seasons) {
                    tx.run(
                        """
                        CREATE (e:Season {
                            identifier: $1,
                            label: $2,
                            releaseDate: $3,
                            thumbnail: $4
                        })
                        """.trimIndent(),
                        mapOf(
                            "1" to s.identifier.toString(),
                            "2" to s.label,
                            "3" to s.releaseDate,
                            "4" to s.thumbnail,
                        )
                    )

                    // Create relationship between TVShow and Season
                    tx.run(
                        """
                        MATCH (tv:TVShow {identifier: $1})
                        MATCH (s:Season {identifier: $2})
                        CREATE (tv)-[:HAS]->(s)
                        """.trimIndent(),
                        mapOf(
                            "1" to tvShow.identifier.toString(),
                            "2" to s.identifier.toString()
                        )
                    )

                    for (ep in s.episodes) {
                        tx.run(
                            """
                        CREATE (e:Episode {
                            identifier: $1,
                            number: $2,
                            title: $3,
                            description: $4,
                            duration: $5,
                            thumbnail: $6
                        })
                        """.trimIndent(), mapOf(
                                "1" to ep.identifier.toString(),
                                "2" to ep.number,
                                "3" to ep.title,
                                "4" to ep.description,
                                "5" to ep.duration,
                                "6" to ep.thumbnail
                            )
                        )

                        tx.run(
                            """
                        MATCH (s:Season {identifier: $1})
                        MATCH (ep:Episode {identifier: $2})
                        CREATE (s)-[:HAS]->(ep)
                        """.trimIndent(),
                            mapOf(
                                "1" to s.identifier.toString(),
                                "2" to ep.identifier.toString()
                            )
                        )
                    }
                }
            }
        }

    }

    fun resetDatabase() {
        println("reset database started")
        driver.session().use { session ->
            session.executeWrite { tx ->
                tx.run("MATCH (n) DETACH DELETE n")  // Clear existing data
                println("reset database finished")
                null
            }
        }
    }

}