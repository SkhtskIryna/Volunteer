package eu.tutorials.domain.utils

import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.History
import eu.tutorials.domain.model.User

object HistoryUtils {
    fun filterHistoriesByRecipientFullName(
        histories: List<History>,
        helps: List<Help>,
        users: List<User>,
        query: String
    ): List<History> {
        val queryLower = query.trim().lowercase()
        if (queryLower.isEmpty()) return emptyList()

        val helpMap = helps.associateBy { it.id!! }
        val userMap = users.associateBy { it.idUser }

        return histories.filter { history ->
            val help = helpMap[history.idRequest]
            val recipient = help?.let { userMap[it.idRecipient] }
            recipient != null && run {
                val fullName = "${recipient.firstName} ${recipient.lastName}".lowercase()
                val reversedFullName = "${recipient.lastName} ${recipient.firstName}".lowercase()
                fullName.contains(queryLower) || reversedFullName.contains(queryLower)
            }
        }
    }
}