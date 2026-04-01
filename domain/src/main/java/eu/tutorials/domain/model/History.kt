package eu.tutorials.domain.model

import eu.tutorials.domain.security.HelpIdentifiable
import eu.tutorials.domain.security.UserIdentifiable
import kotlinx.serialization.Serializable

@Serializable
data class History(
    val id: Int? = null,
    val status: HistoryStatus,
    val idAdmin: Int?,
    val idRequest: Int
): UserIdentifiable, HelpIdentifiable{
    override val idUser: Int?
        get() = idAdmin
    override val idHelp: Int
        get() = idRequest
}
