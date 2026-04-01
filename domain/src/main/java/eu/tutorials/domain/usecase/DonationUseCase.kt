package eu.tutorials.domain.usecase

import eu.tutorials.domain.model.Donation
import eu.tutorials.domain.model.UserRole
import eu.tutorials.domain.repository.IDonationRepository
import eu.tutorials.domain.usecase.generics.CreateUseCase
import eu.tutorials.domain.usecase.generics.DeleteUseCase
import eu.tutorials.domain.usecase.generics.FindByIdUseCase
import eu.tutorials.domain.usecase.generics.GetAllUseCase

class CreateDonationUseCase(iDonationRepository: IDonationRepository): CreateUseCase<Donation>(iDonationRepository, listOf<UserRole>(UserRole.Volunteer))
class DeleteDonationUseCase(iDonationRepository: IDonationRepository): DeleteUseCase<Donation>(iDonationRepository, listOf<UserRole>(UserRole.Volunteer))
class FindDonationByIdUseCase(iDonationRepository: IDonationRepository): FindByIdUseCase<Donation>(iDonationRepository, listOf<UserRole>(UserRole.Volunteer))
class GetAllDonationsUseCase(iDonationRepository: IDonationRepository): GetAllUseCase<Donation>(iDonationRepository, listOf<UserRole>(UserRole.Volunteer))
