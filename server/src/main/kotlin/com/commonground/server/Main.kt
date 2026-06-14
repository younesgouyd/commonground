package com.commonground.server

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class CommonGroundApplication {

    // FOR TESTING
//    @Bean
//    fun runner(dataInitializer: DataInitializer) = CommandLineRunner {
//        dataInitializer.populateTestData()
//    }
}

fun main(args: Array<String>) {
    runApplication<CommonGroundApplication>(*args)
}
