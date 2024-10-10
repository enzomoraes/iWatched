package com.iwatched.api.interfaces.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.iwatched.api.DatabaseSeeder
import com.iwatched.api.domain.dto.*
import com.iwatched.api.domain.repositories.EpisodeRepository
import com.iwatched.api.domain.repositories.SeasonRepository
import com.iwatched.api.domain.repositories.TVShowRepository
import com.iwatched.api.domain.repositories.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val databaseSeeder: DatabaseSeeder,
    private val tvShowRepository: TVShowRepository,
    private val seasonRepository: SeasonRepository,
    private val episodeRepository: EpisodeRepository,
    private val userRepository: UserRepository
) {

    @BeforeEach
    fun setup() {
        databaseSeeder.resetDatabase()
        databaseSeeder.seedUser()
    }

    @Test
    fun `should find all users`() {
        mockMvc.perform(get("/users?page=0&size=10"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content[0].identifier").exists())
    }

    @Test
    fun `should find user by identifier and with timeWatched`() {
        mockMvc.perform(get("/users/{id}", DatabaseSeeder.USER1_ID))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.identifier").value(DatabaseSeeder.USER1_ID.toString()))
            .andExpect(jsonPath("$.timeWatched").exists())

    }

    @Test
    fun `should create a new user`() {
        val userCreateDTO = UserCreateDTO(
            uid = "user123",
            name = "New User",
            email = "newuser@example.com",
            image = "profile.jpg",
            isActive = true
        )
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userCreateDTO))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.identifier").exists())
    }

    @Test
    fun `should update user details`() {
        val userUpdateDTO = UserUpdateDTO(
            name = "Updated Name",
            username = "updated_username"
        )
        mockMvc.perform(
            put("/users/{id}", DatabaseSeeder.USER1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.identifier").value(DatabaseSeeder.USER1_ID.toString()))
    }

    @Test
    fun `should delete user`() {
        mockMvc.perform(delete("/users/{id}", DatabaseSeeder.USER1_ID))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should activate user`() {
        mockMvc.perform(patch("/users/{id}/activate", DatabaseSeeder.USER1_ID))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should deactivate user`() {
        mockMvc.perform(patch("/users/{id}/deactivate", DatabaseSeeder.USER1_ID))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should follow user`() {
        val followRequestDTO = FollowRequestDTO(
            followerId = DatabaseSeeder.USER1_ID,
            followeeId = DatabaseSeeder.USER2_ID
        )

        mockMvc.perform(
            post("/users/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequestDTO))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should watch a whole TV Show`() {
        databaseSeeder.seedTVShow()
        val tvShow = tvShowRepository.findAll().first()
        val watchEpisodeRequestDTO = WatchTVShowDTO(
            DatabaseSeeder.USER1_ID,
            tvShow.identifier
        )

        mockMvc.perform(
            post("/users/watch-tv-show")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(watchEpisodeRequestDTO))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should watch a whole Season`() {
        databaseSeeder.seedTVShow()
        val season = seasonRepository.findAll().first()
        val watchEpisodeRequestDTO = WatchSeasonDTO(
            DatabaseSeeder.USER1_ID,
            season.identifier
        )

        mockMvc.perform(
            post("/users/watch-season")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(watchEpisodeRequestDTO))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `should watch an episode`() {
        databaseSeeder.seedTVShow()
        val episode = episodeRepository.findAll().first()
        val watchEpisodeRequestDTO = WatchEpisodeRequestDTO(
            DatabaseSeeder.USER1_ID,
            episode.identifier
        )

        mockMvc.perform(
            post("/users/watch-episode")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(watchEpisodeRequestDTO))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `watch all seasons episodes then mark season as watched`() {
        databaseSeeder.seedTVShow()
        assertEquals(0, userRepository.findById(DatabaseSeeder.USER1_ID).get().seasons.size)

        val episodes = episodeRepository.findAll()
        for (ep in episodes) {
            val watchEpisodeRequestDTO = WatchEpisodeRequestDTO(
                DatabaseSeeder.USER1_ID,
                ep.identifier
            )
            mockMvc.perform(
                post("/users/watch-episode")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(watchEpisodeRequestDTO))
            )
                .andExpect(status().isNoContent)
        }

        val test = userRepository.getWatchedSeasonIfAllEpisodesWatched(DatabaseSeeder.USER1_ID, episodes.first().identifier)
        println("->>>>>>>>>>>>>>>> episode id ${episodes.first().identifier} teste $test")
        assertEquals(1, userRepository.findById(DatabaseSeeder.USER1_ID).get().seasons.size)
    }

    @Test
    fun `watch all seasons then mark tv show as watched`() {
        databaseSeeder.seedTVShow()
        assertEquals(0, userRepository.findById(DatabaseSeeder.USER1_ID).get().tvShows.size)
        assertEquals(0, userRepository.findById(DatabaseSeeder.USER1_ID).get().seasons.size)

        val seasons = seasonRepository.findAll()
        for (season in seasons) {
            val watchSeasonDTO = WatchSeasonDTO(
                DatabaseSeeder.USER1_ID,
                season.identifier
            )
            mockMvc.perform(
                post("/users/watch-season")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(watchSeasonDTO))
            )
                .andExpect(status().isNoContent)
        }

        assertEquals(5, userRepository.findById(DatabaseSeeder.USER1_ID).get().seasons.size)
        assertEquals(1, userRepository.findById(DatabaseSeeder.USER1_ID).get().tvShows.size)
    }
}
