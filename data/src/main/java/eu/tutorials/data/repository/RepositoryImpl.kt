package eu.tutorials.data.repository

import eu.tutorials.data.mapper.DomainMappable
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass

open class RepositoryImpl<E, D>(
    private val entityClass: IntEntityClass<E>
) where E : IntEntity, E : DomainMappable<D> {

    open suspend fun findById(id: Int): D? = transaction {
        entityClass.findById(id)?.entityToDomain()
    }

    open suspend fun getAll(): List<D> = transaction {
        entityClass.all().map { it.entityToDomain() }
    }

    open suspend fun delete(id: Int): Boolean = transaction {
        entityClass.findById(id)?.let {
            it.delete()
            true
        } ?: false
    }
}
