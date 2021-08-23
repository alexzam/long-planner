package com.github.alexzam.longplanner

import com.github.alexzam.longplanner.model.Counter
import com.github.alexzam.longplanner.model.Plan
import com.github.alexzam.longplanner.model.ShortPlan
import com.github.alexzam.longplanner.model.Var
import com.mongodb.client.model.ReturnDocument
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

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

    suspend fun getAllPlansShort(): List<ShortPlan> =
        plans.withDocumentClass<ShortPlan>()
            .find()
            .ascendingSort(ShortPlan::id)
            .projection(ShortPlan::id, ShortPlan::name)
            .toList()

    suspend fun getPlan(planId: Long): Plan? =
        plans.findOneById(planId)

    suspend fun addVarToPlan(planId: Long, varr: Var) {
        plans.updateOneById(planId, push(Plan::vars, varr))
    }

    private suspend fun getCounterValue(id: String): Long =
        counters.findOneAndUpdate(
            Counter::id eq id,
            inc(Counter::value, 1L),
            findOneAndUpdateUpsert().returnDocument(ReturnDocument.AFTER)
        )!!.value
}