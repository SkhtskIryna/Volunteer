package eu.tutorials.data.mapper

interface DomainMappable<D> {
    fun entityToDomain(): D
}
