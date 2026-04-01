package eu.tutorials.domain.model

import eu.tutorials.domain.security.UserIdentifiable
import eu.tutorials.domain.utils.LocalDateSerializer
import eu.tutorials.domain.utils.LocalDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
sealed class Help : UserIdentifiable {
    abstract val id: Int?
    abstract val title: String
    abstract val description: String
    abstract val idRecipient: Int
    @Serializable(with = LocalDateTimeSerializer::class)
    abstract val createdAt: LocalDateTime
    @Serializable(with = LocalDateTimeSerializer::class)
    abstract val updatedAt: LocalDateTime?

    override val idUser: Int get() = idRecipient

    @Serializable
    @SerialName("Financial")
    data class Financial(
        override val id: Int? = null,
        override val title: String,
        override val description: String,
        override val idRecipient: Int,

        @Serializable(with = LocalDateSerializer::class)
        val from: LocalDate,
        @Serializable(with = LocalDateSerializer::class)
        val to: LocalDate,
        val plannedAmount: Double,
        val collected: Double? = null,

        @Serializable(with = LocalDateTimeSerializer::class)
        override val createdAt: LocalDateTime,
        @Serializable(with = LocalDateTimeSerializer::class)
        override val updatedAt: LocalDateTime? = null
    ) : Help()

    @Serializable
    @SerialName("Material")
    data class Material(
        override val id: Int? = null,
        override val title: String,
        override val description: String,
        override val idRecipient: Int,

        val category: String,
        val region: String?,
        val area: String?,
        val city: String?,

        @Serializable(with = LocalDateTimeSerializer::class)
        override val createdAt: LocalDateTime,
        @Serializable(with = LocalDateTimeSerializer::class)
        override val updatedAt: LocalDateTime? = null
    ) : Help()
}