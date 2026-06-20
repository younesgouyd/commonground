package com.commonground.server.data.repositories

import com.commonground.server.data.entities.User
import com.commonground.server.data.entities.UserFollow
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserFollowRepository : JpaRepository<UserFollow, UUID> {
    fun deleteByFollowerAndFollowee(follower: User, followee: User)
}