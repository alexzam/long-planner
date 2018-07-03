package com.github.alexzam.home.retirementplanner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@SpringBootApplication
@EnableMongoRepositories
class RetirementPlannerApplication

fun main(args: Array<String>) {
    runApplication<RetirementPlannerApplication>(*args)
}
