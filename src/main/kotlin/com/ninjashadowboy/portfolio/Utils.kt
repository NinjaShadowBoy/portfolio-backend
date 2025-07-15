package com.ninjashadowboy.portfolio

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object myProfiler {
    private val log: Logger = LoggerFactory.getLogger(javaClass)
    fun <T> profileOperation(name: String = "Operation", operation: () -> T): T {
        val start = System.nanoTime()
        val result = operation()
        val end = System.nanoTime()

        val time = (end - start) / 1_000_000
        log.info("$name took $time ms")
        return result
    }
}

