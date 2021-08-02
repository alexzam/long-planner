package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.World
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

class StorageService {

    val db = KMongo.createClient().coroutine.getDatabase("planner")
    val worlds = db.getCollection<World>()
}