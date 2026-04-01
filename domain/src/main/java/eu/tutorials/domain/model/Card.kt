package eu.tutorials.domain.model

import eu.tutorials.domain.security.UserIdentifiable
import eu.tutorials.domain.utils.YearMonthSerializer
import kotlinx.serialization.Serializable
import java.time.YearMonth

@Serializable
data class Card(
    val id: Int? = null,
    val number: String,
    @Serializable(with = YearMonthSerializer::class)
    val validityPeriod: YearMonth,
    val idRecipient: Int
): UserIdentifiable {
    override val idUser: Int
        get() = idRecipient
}
