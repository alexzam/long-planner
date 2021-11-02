package com.github.alexzam.longplanner.dao

import com.github.alexzam.longplanner.model.Counter
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.findOneAndUpdateUpsert
import org.litote.kmongo.inc

interface CounterDao {
    suspend fun getCounterValue(id: String): Long
}

internal class CounterDaoImpl(db: CoroutineDatabase) : CounterDao {
    private val counters = db.getCollection<Counter>()

    override suspend fun getCounterValue(id: String): Long =
        counters.findOneAndUpdate(
            Counter::id eq id,
            inc(Counter::value, 1L),
            findOneAndUpdateUpsert().returnDocument(ReturnDocument.AFTER)
        )!!.value
}