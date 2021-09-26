package com.github.alexzam.longplanner.dao

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assumptions
import org.junit.jupiter.api.BeforeEach
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import java.time.Duration

abstract class DbTest {
    protected val db = KMongo.createClient().coroutine.getDatabase("planner-test")

    @BeforeEach
    fun assumeDbIsAccessible() {
        Assumptions.assumeTrue({
            try {
                runBlocking {
                    withTimeout(Duration.ofSeconds(1))
                    {
                        db.listCollectionNames()
                    }
                    true
                }
            } catch (e: Throwable) {
                println(e)
                false
            }
        }, "DB is not accessible")
    }

    @AfterEach
    fun after() {
        runBlocking {
            withTimeoutOrNull(1000) {
                db.drop()
            }
        }
    }
}