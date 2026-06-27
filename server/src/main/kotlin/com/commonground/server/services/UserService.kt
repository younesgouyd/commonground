package com.commonground.server.services

import com.commonground.core.models.User
import com.commonground.core.models.Users
import com.commonground.server.data.entities.UserFollow
import com.commonground.server.data.repositories.UserFollowRepository
import com.commonground.server.data.repositories.UserRepository
import com.commonground.server.util.toUuid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import kotlin.jvm.optionals.getOrNull

@Service
class UserService(
    private val userRepository: UserRepository,
    private val userFollowRepository: UserFollowRepository,
    private val imageService: ImageService
) {
    @Transactional
    fun getUserWithFollowState(userId: String, followStateUserId: String): User? {
        return userRepository.findByIdWithFollowState(userId.toUuid(), followStateUserId.toUuid())
    }

    @Transactional
    fun getFollowersWithFollowState(
        followeeId: String,
        followStateUserId: String,
        pageNumber: Int
    ): Users {
        val followStateUser = userRepository.getReferenceById(followStateUserId.toUuid())
        val followee = userRepository.getReferenceById(followeeId.toUuid())
        val users = userRepository.findFollowersWithFollowState(
            followStateUser = followStateUser,
            followee = followee,
            pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE)
        )
        return Users(
            items = users.content,
            next = if (users.hasNext()) users.nextPageable().pageNumber else null,
            total = users.totalElements
        )
    }

    @Transactional
    fun getFolloweesWithFollowState(
        followerId: String,
        followStateUserId: String,
        pageNumber: Int
    ): Users {
        val follower = userRepository.getReferenceById(followerId.toUuid())
        val followStateUser = userRepository.getReferenceById(followStateUserId.toUuid())
        val users = userRepository.findFolloweesWithFollowState(
            follower = follower,
            followStateUser = followStateUser,
            pageable = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE)
        )
        return Users(
            items = users.content,
            next = if (users.hasNext()) users.nextPageable().pageNumber else null,
            total = users.totalElements
        )
    }

    @Transactional
    fun followUser(
        followerId: String,
        followeeId: String
    ) {
        userFollowRepository.save(
            UserFollow(
                follower = userRepository.getReferenceById(followerId.toUuid()),
                followee = userRepository.getReferenceById(followeeId.toUuid())
            )
        )
    }

    @Transactional
    fun unfollowUser(
        followerId: String,
        followeeId: String
    ) {
        userFollowRepository.deleteByFollowerAndFollowee(
            follower = userRepository.getReferenceById(followerId.toUuid()),
            followee = userRepository.getReferenceById(followeeId.toUuid())
        )
    }

    private fun Page<User>.toModel(): Users {
        return Users(
            items = this.content,
            next = if (this.hasNext()) this.nextPageable().pageNumber else null,
            total = this.totalElements
        )
    }

    @Transactional
    fun update(
        id: String,
        username: String,
        displayName: String?,
        bio: String?
    ) {
        userRepository.update(
            id = id.toUuid(),
            username = username,
            displayName = displayName,
            bio = bio
        )
    }

    @Transactional
    fun updateProfilePic(
        id: String,
        file: MultipartFile
    ) {
        val user = userRepository.findById(id.toUuid()).getOrNull() ?: return
        val imgUrl = imageService.store(file, "profile_pic")
        userRepository.updateProfilePic(id.toUuid(), imgUrl)
        user.profilePic?.let {
            imageService.delete(it)
        }
    }

    @Transactional
    fun clearProfilePic(id: String) {
        val user = userRepository.findById(id.toUuid()).getOrNull() ?: return
        user.profilePic?.let {
            imageService.delete(it)
            userRepository.updateProfilePic(id.toUuid(), null)
        }
    }
}
