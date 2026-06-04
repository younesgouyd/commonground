package com.commonground.server.configuration

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate

@Configuration
class SpatialIndexInitializer(private val jdbcTemplate: JdbcTemplate) {
    @Bean
    fun createSpatialIndexesRunner() = CommandLineRunner {
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_event_coordinates_geog ON event USING gist (coordinates);")
    }
}