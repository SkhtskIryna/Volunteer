package eu.tutorials.volunteerapp.data

import eu.tutorials.domain.model.Help
import eu.tutorials.domain.model.History
import eu.tutorials.domain.model.HistoryStatus

data class HelpWithHistory(
    val help: Help,
    val history: History,
    val status: HistoryStatus
)
