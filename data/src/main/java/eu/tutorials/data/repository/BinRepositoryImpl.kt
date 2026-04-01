package eu.tutorials.data.repository

import eu.tutorials.data.datasource.entity.BinEntity
import eu.tutorials.data.datasource.entity.UserEntity
import eu.tutorials.domain.model.Bin
import eu.tutorials.domain.model.User
import eu.tutorials.domain.repository.IBinRepository
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class BinRepositoryImpl: RepositoryImpl<BinEntity, Bin>(BinEntity), IBinRepository {
    override suspend fun create(bin: Bin): Bin = transaction{
        val userEntity = UserEntity.findById(bin.idUser)

        requireNotNull(userEntity){
            "User with id ${bin.idUser} not found"
        }

        BinEntity.new {
            this.id_recipient = userEntity
            this.added_at = LocalDateTime.now()
        }.entityToDomain()
    }

    override suspend fun unblockedUser(id: Int): User = transaction {
        val userEntity = UserEntity.findById(id)
            ?: error("User with id $id not found")

        userEntity.is_blocked = true
        userEntity.entityToDomain()
    }
}