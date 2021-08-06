package com.github.alexzam.longplanner

import com.github.alexzam.longplanner.model.Counter
import com.github.alexzam.longplanner.model.Plan
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.findOneAndUpdateUpsert
import org.litote.kmongo.inc
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.setValue

class StorageService {

    val db = KMongo.createClient().coroutine.getDatabase("planner")
    private val counters = db.getCollection<Counter>()
    private val plans = db.getCollection<Plan>()


    suspend fun createPlan(): Plan {
        val plan = Plan.makeNew(getCounterValue("plans"))
        val result = plans.insertOne(plan)
        if (!result.wasAcknowledged()) throw Exception("Mongo write not acknowledged")

        return plan
    }

    suspend fun updateName(planId: Long, newName: String) {
        plans.updateOne(Plan::id eq planId, setValue(Plan::name, newName))
    }

    private suspend fun getCounterValue(id: String): Long {
        return counters.findOneAndUpdate(
            Counter::id eq id,
            inc(Counter::value, 1L),
            findOneAndUpdateUpsert().returnDocument(ReturnDocument.AFTER)
        )!!.value
    }
}