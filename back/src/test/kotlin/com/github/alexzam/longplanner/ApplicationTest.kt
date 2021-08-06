package com.github.alexzam.longplanner

import com.example.plugins.configureRouting
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ configureRouting(storageService) }) {
            handleRequest(HttpMethod.Get, "/").apply {
//                assertEquals(HttpStatusCode.OK, response.status())
//                assertEquals("Hello World!", response.content)
            }
        }
    }
}