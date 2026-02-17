package com.ninjashadowboy.portfolio.controllers.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CronController : BaseController() {

    @GetMapping("/cron")
    fun cron() = "Portfolio backend alive"
}