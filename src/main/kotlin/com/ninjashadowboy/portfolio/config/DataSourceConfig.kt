package com.ninjashadowboy.portfolio.config

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource
import java.net.URI

@Configuration
@Profile("prod")
class DataSourceConfig {

    @Bean
    fun dataSource(): DataSource {
        // Render provides DATABASE_URL in format: postgres://user:pass@host:port/db
        // Spring Boot needs: jdbc:postgresql://host:port/db with proper credentials
        val databaseUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/portfolio"

        return if (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://")) {
            // Parse the Render database URL
            val dbUri = URI(databaseUrl.replace("postgres://", "postgresql://"))
            val username = dbUri.userInfo.split(":")[0]
            val password = dbUri.userInfo.split(":")[1]
            val host = dbUri.host
            val port = if (dbUri.port != -1) dbUri.port else 5432
            val database = dbUri.path.substring(1) // Remove leading /

            // Construct proper JDBC URL
            val jdbcUrl = "jdbc:postgresql://$host:$port/$database"

            DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build()
        } else {
            // Fallback for already properly formatted JDBC URLs
            DataSourceBuilder.create()
                .url(databaseUrl)
                .build()
        }
    }
}
