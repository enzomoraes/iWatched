package com.iwatched.api.interfaces.http

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/health")
class HealthCheckController {

    @GetMapping
    fun checkHealth(): Map<String, String> {
        return mapOf("status" to "UP")
    }
}
