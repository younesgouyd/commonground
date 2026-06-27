package com.commonground.server.data.repositories

import com.commonground.server.data.entities.UserEvent
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserEventRepository : JpaRepository<UserEvent, UUID>