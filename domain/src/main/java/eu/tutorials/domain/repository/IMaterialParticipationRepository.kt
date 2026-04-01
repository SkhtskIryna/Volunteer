package eu.tutorials.domain.repository

import eu.tutorials.domain.model.MaterialParticipation

interface IMaterialParticipationRepository: IRepository<MaterialParticipation> {
    suspend fun update(materialParticipation: MaterialParticipation): Boolean
}