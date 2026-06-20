package com.commonground.server.data.repositories

import com.commonground.server.data.entities.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsernameOrEmailAddress(username: String, emailAddress: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByEmailAddress(emailAddress: String): Boolean

    /**
     * Retrieves a paginated list of users who follow the specified [followee].
     *
     * For each follower returned, it determines if the [followStateUser] (typically the
     * currently logged-in user) is also following them, populating the `isFollowed` flag.
     *
     * @param followStateUser The user observing the list, used to determine the `isFollowed` state.
     * @param followee The user whose followers are being retrieved.
     * @param pageable Pagination configuration.
     * @return A page of [com.commonground.core.models.User] models.
     */
    @Query(
        """
            SELECT 
                CAST(uf.follower.id AS string), uf.follower.username, uf.follower.displayName, uf.follower.bio, uf.follower.emailAddress, uf.follower.profilePic,
                CASE WHEN (SELECT 1 FROM UserFollow uf2 WHERE uf2.follower = :followStateUser AND uf2.followee = uf.follower) IS NOT NULL THEN true ELSE false END AS isFollowed
            FROM UserFollow uf
            WHERE uf.followee = :followee
            ORDER BY uf.createdAt DESC, uf.id
        """
    )
    fun findFollowersWithFollowState(
        @Param("followee") followee: User,
        @Param("followStateUser") followStateUser: User,
        pageable: Pageable
    ): Page<com.commonground.core.models.User>

    /**
     * Retrieves a paginated list of users that the specified [follower] is following.
     *
     * For each followee returned, it determines if the [followStateUser] (typically the
     * currently logged-in user) is also following them, populating the `isFollowed` flag.
     *
     * @param followStateUser The user observing the list, used to determine the `isFollowed` state.
     * @param follower The user whose followings (followees) are being retrieved.
     * @param pageable Pagination configuration.
     * @return A page of [com.commonground.core.models.User] models.
     */
    @Query(
        """
        SELECT 
            CAST(uf.followee.id AS string), uf.followee.username, uf.followee.displayName, uf.followee.bio, uf.followee.emailAddress, uf.followee.profilePic,
            CASE WHEN (SELECT 1 FROM UserFollow uf2 WHERE uf2.follower = :followStateUser AND uf2.followee = uf.followee) IS NOT NULL THEN true ELSE false END AS isFollowed
        FROM UserFollow uf
        WHERE uf.follower = :follower
        ORDER BY uf.createdAt DESC, uf.id
    """
    )
    fun findFolloweesWithFollowState(
        @Param("follower") follower: User,
        @Param("followStateUser") followStateUser: User,
        pageable: Pageable
    ): Page<com.commonground.core.models.User>
}