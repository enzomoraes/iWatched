package com.iwatched.api

import org.neo4j.driver.Driver
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("test")
class DatabaseSeeder @Autowired constructor(private val driver: Driver) {

    companion object UserIdentifiers {
        const val USER1_ID = "fa4fb627-d0d1-484e-9c8c-a08d674d4f68"
        const val USER2_ID = "13a0d0ef-0ba6-4d53-ab56-27c8880c19b6"
    }

    fun seed() {
        println("seeding database")
        driver.session().use { session ->
            session.executeWrite { tx ->
                tx.run("MATCH (n) DETACH DELETE n")  // Clear existing data
                tx.run("CREATE (u1:User {identifier: '${USER1_ID}', uid: 'user1', name: 'John Doe', email: 'john@example.com', image: 'john.jpg', active: true})")
                tx.run("CREATE (u2:User {identifier: '${USER2_ID}', uid: 'user2', name: 'Elon Musk', email: 'elon@example.com', image: 'elon.jpg', active: true})")
                tx.run("MATCH (u1:User {identifier: '${USER1_ID}'}), (u2:User {identifier: '${USER2_ID}'}) CREATE (u1)-[:FOLLOWS]->(u2)")

                println("Seeding database is finished")
                null
            }
        }
    }
}