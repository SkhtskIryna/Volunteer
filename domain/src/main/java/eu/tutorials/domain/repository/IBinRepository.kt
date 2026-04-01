package eu.tutorials.domain.repository

import eu.tutorials.domain.model.Bin
import eu.tutorials.domain.model.User

interface IBinRepository: IRepository<Bin>{
    suspend fun unblockedUser(id: Int): User
}