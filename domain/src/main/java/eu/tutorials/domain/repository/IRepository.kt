package eu.tutorials.domain.repository

interface IRepository<T> {
    suspend fun create(item: T): T
    suspend fun findById(id: Int): T?
    suspend fun getAll(): List<T>
    suspend fun delete(id: Int): Boolean
}