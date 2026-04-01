package eu.tutorials.domain.repository

import eu.tutorials.domain.model.Help

interface IHelpRepository: IRepository<Help> {
    suspend fun update(help: Help): Boolean
}