package eu.tutorials.data.repository

import eu.tutorials.data.datasource.entity.UserEntity
import eu.tutorials.data.datasource.table.UserTable
import eu.tutorials.domain.model.User
import eu.tutorials.domain.repository.IUserRepository
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class UserRepositoryImpl: RepositoryImpl<UserEntity, User>(UserEntity), IUserRepository {
    override suspend fun create(user: User): User = transaction {
        UserEntity.new {
            this.first_name = user.firstName
            this.last_name = user.lastName
            this.role = user.role
            this.phone = user.phone
            this.email = user.email
            this.password_hash = user.password.toString()
            this.telegram = user.telegram
            this.created_at = LocalDateTime.now()
        }.entityToDomain()
    }

    override suspend fun getPasswordHash(email: String): String? = transaction {
        UserEntity.find { UserTable.email eq email }
            .firstOrNull()?.password_hash
    }

    override suspend fun findByEmail(email: String): User? = transaction{
        UserEntity.find { UserTable.email eq email }.firstOrNull()?.entityToDomain()
    }

    override suspend fun update(user: User): Boolean = transaction {
        val userEntity = UserEntity.findById(user.id!!) ?: return@transaction false

        userEntity.apply {
            first_name = user.firstName.takeIf { it.isNotBlank() } ?: first_name
            last_name = user.lastName.takeIf { it.isNotBlank() } ?: last_name
            phone = user.phone.takeIf { it.isNotBlank() } ?: phone
            email = user.email.takeIf { it.isNotBlank() } ?: email
            telegram = user.telegram?.takeIf { it.isNotBlank() } ?: telegram
            photoBase64 = user.photoBase64?.takeIf { it.isNotBlank() }
            updated_at = LocalDateTime.now()

            user.password?.let { newPassword ->
                if (newPassword.isNotBlank()) {
                    password_hash = newPassword
                }
            }
        }

        true
    }

    override suspend fun updateBlockStatus(userId: Int, isBlocked: Boolean): Boolean = transaction {
        val userEntity = UserEntity.findById(userId) ?: return@transaction false

        userEntity.is_blocked = isBlocked
        userEntity.updated_at = LocalDateTime.now()

        true
    }

    fun findEntityByEmail(email: String): UserEntity? = transaction {
        UserEntity.find { UserTable.email eq email }.firstOrNull()
    }
}