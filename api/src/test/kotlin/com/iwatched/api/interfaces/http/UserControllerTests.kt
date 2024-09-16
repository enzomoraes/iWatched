package com.iwatched.api.interfaces.http

import com.fasterxml.jackson.databind.ObjectMapper
import com.iwatched.api.DatabaseSeeder
import com.iwatched.api.domain.dto.FollowRequestDTO
import com.iwatched.api.domain.dto.UserCreateDTO
import com.iwatched.api.domain.dto.UserUpdateDTO
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
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    private val databaseSeeder: DatabaseSeeder,
) {

    @BeforeEach
    fun setup() {
        databaseSeeder.seed()  // Seed database before each test
    }

    @Test
    fun `should find all users`() {
        mockMvc.perform(get("/users?page=0&size=10"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content[0].identifier").exists())
    }

    @Test
    fun `should find user by identifier`() {
        mockMvc.perform(get("/users/{id}", DatabaseSeeder.USER1_ID))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.identifier").value(DatabaseSeeder.USER1_ID))
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
            .andExpect(jsonPath("$.identifier").value(DatabaseSeeder.USER1_ID))
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
            followerId = UUID.fromString(DatabaseSeeder.USER1_ID),
            followeeId = UUID.fromString(DatabaseSeeder.USER2_ID)
        )

        mockMvc.perform(
            post("/users/follow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(followRequestDTO))
        )
            .andExpect(status().isNoContent)
    }
}
