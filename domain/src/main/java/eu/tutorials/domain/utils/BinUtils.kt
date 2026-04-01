package eu.tutorials.domain.utils

import eu.tutorials.domain.model.Bin
import eu.tutorials.domain.model.User

object BinUtils {
    fun filterBinsByRecipientFullName(bins: List<Bin>,
                                 users: List<User>, query: String): List<Bin> {
        val queryLower = query.trim().lowercase()
        if (queryLower.isEmpty()) return emptyList()

        val userMap = users.associateBy { it.idUser }

        return bins.filter { bin ->
            val user = userMap[bin.idRecipient]
            user != null && run {
                val fullName = "${user.firstName} ${user.lastName}".lowercase()
                val reversedFullName = "${user.lastName} ${user.firstName}".lowercase()
                fullName.contains(queryLower) || reversedFullName.contains(queryLower)
            }
        }
    }
}