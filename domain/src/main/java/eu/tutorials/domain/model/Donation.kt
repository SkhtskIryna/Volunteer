package eu.tutorials.domain.model

import eu.tutorials.domain.security.HelpIdentifiable
import eu.tutorials.domain.security.UserIdentifiable
import kotlinx.serialization.Serializable

@Serializable
data class Donation(
    val id: Int? = null,
    val sum: Double,
    val idVolunteer: Int,
    val idFinancialRequest: Int
): UserIdentifiable, HelpIdentifiable{
    override val idUser: Int
        get() = idVolunteer
    override val idHelp: Int
        get() = idFinancialRequest
}
