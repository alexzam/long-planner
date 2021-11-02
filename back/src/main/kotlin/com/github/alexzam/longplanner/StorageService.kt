package com.github.alexzam.longplanner

import com.github.alexzam.longplanner.dao.CounterDaoImpl
import com.github.alexzam.longplanner.dao.PlanDao
import com.github.alexzam.longplanner.dao.TimepointsDao
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class StorageService {
    val db = KMongo.createClient().coroutine.getDatabase("planner")

    private val counterDao = CounterDaoImpl(db)
    val plans = PlanDao(db, counterDao)
    val timepoints = TimepointsDao(db, counterDao)
}