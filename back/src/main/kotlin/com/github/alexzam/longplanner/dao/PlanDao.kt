package com.github.alexzam.longplanner.dao

import com.github.alexzam.longplanner.model.Plan
import com.github.alexzam.longplanner.model.ShortPlan
import com.github.alexzam.longplanner.model.Var
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import io.ktor.features.*
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.push
import org.litote.kmongo.setValue

class PlanDao(
    db: CoroutineDatabase,
    private val counterDao: CounterDao
) {
    private val plans = db.getCollection<Plan>()

    suspend fun createPlan(): Plan {
        val plan = Plan.makeNew(counterDao.getCounterValue("plans"))
        val result = plans.insertOne(plan)
        if (!result.wasAcknowledged()) throw Exception("Mongo write not acknowledged")

        return plan
    }

    suspend fun getAllPlansShort(): List<ShortPlan> =
        plans.withDocumentClass<ShortPlan>()
            .find()
            .ascendingSort(ShortPlan::id)
            .projection(ShortPlan::id, ShortPlan::name)
            .toList()

    suspend fun getPlan(planId: Long): Plan? =
        plans.findOneById(planId)

    suspend fun getPlanOrFail(planId: Long): Plan =
        plans.findOneById(planId) ?: throw NotFoundException("Plan not found")

    suspend fun addVarToPlan(planId: Long, varr: Var) {
        plans.updateOneById(planId, push(Plan::vars, varr))
    }

    suspend fun updateVar(planId: Long, varId: Int, variable: Var): Plan {
        val plan = getPlanOrFail(planId)
        val newVars = plan.vars
            .map {
                if (it.id != varId) it
                else variable
            }
        return plans.findOneAndUpdate(
            Plan::id eq planId,
            setValue(Plan::vars, newVars),
            FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        ) ?: throw NotFoundException("Plan not found")
    }

    /**
     * Does not update vars
     */
    suspend fun updatePlanParams(plan: Plan): Plan {
        val oldPlan = getPlanOrFail(plan.id)
        val newPlan = plan.copy(vars = oldPlan.vars)

        plans.updateOneById(plan.id, newPlan)

        return newPlan
    }
}