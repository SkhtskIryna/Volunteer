package eu.tutorials.data.datasource.entity

import eu.tutorials.data.datasource.table.UserTable
import eu.tutorials.data.mapper.DomainMappable
import eu.tutorials.domain.model.User
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.util.Base64

class UserEntity(id: EntityID<Int>): IntEntity(id), DomainMappable<User>{
    companion object: IntEntityClass<UserEntity>(UserTable)

    var first_name by UserTable.first_name
    var last_name by UserTable.last_name
    var role by UserTable.role
    var phone by UserTable.phone
    var email by UserTable.email
    var password_hash by UserTable.password_hash
    var telegram by UserTable.telegram
    var photo by UserTable.photoBase64
    var photoBase64: String?
        get() = photo?.bytes?.let { Base64.getEncoder().encodeToString(it) }
        set(value) {
            photo = value?.let { ExposedBlob(Base64.getDecoder().decode(it)) }
        }
    var is_blocked by UserTable.isBlocked
    var created_at by UserTable.created_at
    var updated_at by UserTable.updated_at

    override fun entityToDomain(): User = User(
        id = id.value,
        firstName = first_name,
        lastName = last_name,
        role = role,
        phone = phone,
        email = email,
        telegram = telegram,
        photoBase64 = photoBase64,
        password = password_hash,
        isBlocked = is_blocked
    )
}