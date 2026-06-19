package com.commonground.server.services

import com.commonground.core.models.User
import com.commonground.server.data.repositories.UserRepository
import com.commonground.server.util.toModel
import com.commonground.server.util.toUuid
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun getUser(userId: String): User? {
        return userRepository.findById(userId.toUuid()).getOrNull()?.toModel()
    }
}
