package com.iwatched.api.interfaces.http

import com.iwatched.api.DatabaseSeeder
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TVShowControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val databaseSeeder: DatabaseSeeder,
) {

    @BeforeEach
    fun setup() {
        databaseSeeder.resetDatabase()
        databaseSeeder.seedTVShow()
    }

    @Test
    fun `should find all tv shows`() {
        mockMvc.perform(get("/tv-shows?page=0&size=10"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content[0].identifier").exists())
    }

    @Test
    fun `should find no tv shows`() {
        mockMvc.perform(get("/tv-shows?page=0&size=10&title=not-registered"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.content[0].identifier").doesNotExist())
            .andExpect(jsonPath("$.totalElements").value("0"))
    }
}
