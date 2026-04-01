package eu.tutorials.volunteerapp.ui.components

import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.History
import eu.tutorials.volunteerapp.data.HelpWithHistory

fun prepareHelpHistoryList(
    helps: List<Help>,
    histories: List<History>
): List<HelpWithHistory> {
    // Мапа helps за id для швидкого пошуку
    val helpsMap = helps.associateBy { it.id }

    // Формування списку HelpWithHistory
    return histories.mapNotNull { history ->
        val help = helpsMap[history.idRequest] ?: return@mapNotNull null
        HelpWithHistory(
            help = help,
            history = history,
            status = history.status
        )
    }.distinctBy { it.history.id } // Унікальні по id історії
}