package com.ninjashadowboy.portfolio.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import javax.sql.DataSource

@Configuration
@Profile("prod")
class DataSourceConfig {

    @Value("\${DB_HOST:localhost}")
    private lateinit var dbHost: String

    @Value("\${DB_PORT:5432}")
    private lateinit var dbPort: String

    @Value("\${DB_NAME:portfolio}")
    private lateinit var dbName: String

    @Value("\${DB_USER:postgres}")
    private lateinit var dbUser: String

    @Value("\${DB_PASSWORD:password}")
    private lateinit var dbPassword: String

    @Bean
    @Primary
    fun dataSource(): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
        config.username = dbUser
        config.password = dbPassword
        config.driverClassName = "org.postgresql.Driver"
        config.maximumPoolSize = 10
        config.minimumIdle = 5
        config.connectionTimeout = 30000
        config.idleTimeout = 600000

        return HikariDataSource(config)
    }
}
