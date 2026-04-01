package eu.tutorials.data.datasource.table

import eu.tutorials.domain.model.UserRole
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.IColumnType
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import java.time.LocalDateTime

object UserTable: IntIdTable("user"){
    val first_name = varchar("first_name", 50)
    val last_name = varchar("last_name", 50)
    val role = enumerationByName("role", 10, UserRole::class)
    val phone = varchar("phone", 20).uniqueIndex()
    val email = varchar("email", 60).uniqueIndex()
    val password_hash = varchar("password_hash", 225)
    val telegram = varchar("telegram", 45).nullable()
    val photoBase64 =  registerColumn<ExposedBlob>("photo", LongBlobColumnType()).nullable()
    val isBlocked = bool("is_blocked").default(false)
    val created_at = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updated_at = datetime("updated_at").nullable()
}

class LongBlobColumnType(override var nullable: Boolean = true) : IColumnType<ExposedBlob> {

    override fun sqlType(): String = "LONGBLOB"

    override fun valueFromDB(value: Any): ExposedBlob =
        when (value) {
            is ExposedBlob -> value
            is ByteArray -> ExposedBlob(value)
            else -> error("Unexpected value for LONGBLOB: $value")
        }

    override fun notNullValueToDB(value: ExposedBlob): Any = value.bytes
}