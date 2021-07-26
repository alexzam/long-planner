package com.github.alexzam.home.retirementplanner

import com.github.alexzam.home.retirementplanner.model.WorldRepository
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import javax.websocket.server.PathParam

@Controller
@RequestMapping("/api")

class ApiController @Autowired constructor(private val worldRepository: WorldRepository,
                                           private val calcService: CalcService) {

    @RequestMapping("/plans")
    @ResponseBody
    fun getPlans(): String {
        return worldRepository.findAll()
                .map { it.name }
                .joinToString(separator = "\\n")
    }

    @RequestMapping("/plans/{id}")
    @ResponseBody
    fun getPlan(@PathParam("id") id: String) {
        val plan = worldRepository.findById(ObjectId(id)).orElseThrow { Exception("Not found") }

        val points = calcService.calculateWorld(plan)
    }

    @RequestMapping("/temp", produces = ["text/plain"])
    @ResponseBody
    fun temp(): String {
        return "Noop"
    }


}