package com.github.alexzam.home.retirementplanner

import com.github.alexzam.longplanner.model.Plan
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class StorageService {

    val db = KMongo.createClient().coroutine.getDatabase("planner")
    val plans = db.getCollection<Plan>()
}