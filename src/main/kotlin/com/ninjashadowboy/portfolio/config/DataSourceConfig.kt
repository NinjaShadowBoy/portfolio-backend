package com.ninjashadowboy.portfolio.config

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile("prod")
class DataSourceConfig {

    @Bean
    fun dataSource(): DataSource {
        // Render provides DATABASE_URL in format: postgres://user:pass@host:port/db
        // Spring Boot needs: jdbc:postgresql://host:port/db
        val databaseUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/portfolio"

        val jdbcUrl = if (databaseUrl.startsWith("postgres://")) {
            // Convert Render's postgres:// URL to jdbc:postgresql://
            databaseUrl.replace("postgres://", "jdbc:postgresql://")
        } else if (!databaseUrl.startsWith("jdbc:")) {
            // If no jdbc: prefix, add it
            "jdbc:$databaseUrl"
        } else {
            databaseUrl
        }

        return DataSourceBuilder.create()
            .url(jdbcUrl)
            .build()
    }
}
