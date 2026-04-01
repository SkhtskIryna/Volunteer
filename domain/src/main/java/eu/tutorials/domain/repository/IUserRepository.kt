package eu.tutorials.domain.repository

import eu.tutorials.domain.model.User

interface IUserRepository: IRepository<User> {
    suspend fun getPasswordHash(email: String): String?
    suspend fun findByEmail(email: String): User?
    suspend fun update(user: User): Boolean
    suspend fun updateBlockStatus(userId: Int, isBlocked: Boolean): Boolean
}