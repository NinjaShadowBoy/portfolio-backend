package com.ninjashadowboy.portfolio.repositories

import com.ninjashadowboy.portfolio.entities.Project
import org.springframework.data.jpa.repository.JpaRepository

interface ProjectRepo : JpaRepository<Project, Long>