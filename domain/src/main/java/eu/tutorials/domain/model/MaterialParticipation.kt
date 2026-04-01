package eu.tutorials.domain.model

import eu.tutorials.domain.security.HelpIdentifiable
import eu.tutorials.domain.security.UserIdentifiable
import eu.tutorials.domain.utils.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class MaterialParticipation(
    val id: Int? = null,
    val status: MaterialParticipationStatus,
    val idVolunteer: Int,
    val idMaterialRequest: Int,
    @Serializable(with = LocalDateTimeSerializer::class)
    val deliveredAt: LocalDateTime? = null
): UserIdentifiable, HelpIdentifiable {
    override val idUser: Int
        get() = idVolunteer
    override val idHelp: Int
        get() = idMaterialRequest
}
