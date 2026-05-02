package com.commonground.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class commonGroundApplication

fun main(args: Array<String>) {
    runApplication<commonGroundApplication>(*args)
}
