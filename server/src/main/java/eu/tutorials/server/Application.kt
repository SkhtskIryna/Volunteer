package eu.tutorials.server

import eu.tutorials.data.datasource.config.DatabaseFactory
import eu.tutorials.data.datasource.entity.UserEntity
import eu.tutorials.data.repository.BinRepositoryImpl
import eu.tutorials.data.repository.CardRepositoryImpl
import eu.tutorials.data.repository.DonationRepositoryImpl
import eu.tutorials.data.repository.HelpRepositoryImpl
import eu.tutorials.data.repository.HistoryRepositoryImpl
import eu.tutorials.data.repository.MaterialParticipationRepositoryImpl
import eu.tutorials.data.repository.UserRepositoryImpl
import eu.tutorials.domain.model.Help
import eu.tutorials.domain.security.IPasswordHashed
import eu.tutorials.domain.security.IPhotoEncoder
import eu.tutorials.domain.usecase.CreateBinUseCase
import eu.tutorials.domain.usecase.CreateCardUseCase
import eu.tutorials.domain.usecase.CreateDonationUseCase
import eu.tutorials.domain.usecase.CreateHelpUseCase
import eu.tutorials.domain.usecase.CreateHistoryUseCase
import eu.tutorials.domain.usecase.CreateMaterialParticipationUseCase
import eu.tutorials.domain.usecase.CreateUserUseCase
import eu.tutorials.domain.usecase.DeleteBinUseCase
import eu.tutorials.domain.usecase.DeleteCardUseCase
import eu.tutorials.domain.usecase.DeleteDonationUseCase
import eu.tutorials.domain.usecase.DeleteHelpUseCase
import eu.tutorials.domain.usecase.DeleteHistoryUseCase
import eu.tutorials.domain.usecase.DeleteMaterialParticipationUseCase
import eu.tutorials.domain.usecase.DeleteUserUseCase
import eu.tutorials.domain.usecase.FilterBinsByRecipientNameUseCase
import eu.tutorials.domain.usecase.FilterHistoriesByRecipientFullNameUseCase
import eu.tutorials.domain.usecase.FindBinByIdUseCase
import eu.tutorials.domain.usecase.FindCardByIdUseCase
import eu.tutorials.domain.usecase.FindDonationByIdUseCase
import eu.tutorials.domain.usecase.FindHelpByIdUseCase
import eu.tutorials.domain.usecase.FindHistoryByIdUseCase
import eu.tutorials.domain.usecase.FindMaterialParticipationByIdUseCase
import eu.tutorials.domain.usecase.FindUserByEmailUseCase
import eu.tutorials.domain.usecase.FindUserByIdUseCase
import eu.tutorials.domain.usecase.GetAllBinsUseCase
import eu.tutorials.domain.usecase.GetAllCardsUseCase
import eu.tutorials.domain.usecase.GetAllDonationsUseCase
import eu.tutorials.domain.usecase.GetAllHelpsUseCase
import eu.tutorials.domain.usecase.GetAllHistoriesUseCase
import eu.tutorials.domain.usecase.GetAllMaterialParticipationUseCase
import eu.tutorials.domain.usecase.GetAllUsersUseCase
import eu.tutorials.domain.usecase.GetPasswordHashUseCase
import eu.tutorials.domain.usecase.UnblockedUserUseCase
import eu.tutorials.domain.usecase.UpdateCardUseCase
import eu.tutorials.domain.usecase.UpdateHelpUseCase
import eu.tutorials.domain.usecase.UpdateMaterialParticipationUseCase
import eu.tutorials.domain.usecase.UpdateUserBlockUseCase
import eu.tutorials.domain.usecase.UpdateUserUseCase
import eu.tutorials.server.controller.BinController
import eu.tutorials.server.controller.CardController
import eu.tutorials.server.controller.DonationController
import eu.tutorials.server.controller.HelpController
import eu.tutorials.server.controller.HistoryController
import eu.tutorials.server.controller.MaterialParticipationController
import eu.tutorials.server.controller.UserController
import eu.tutorials.server.routes.binRoutes
import eu.tutorials.server.routes.cardRoutes
import eu.tutorials.server.routes.donationRoutes
import eu.tutorials.server.routes.fondyRoutes
import eu.tutorials.server.routes.helpRoutes
import eu.tutorials.server.routes.historyRoutes
import eu.tutorials.server.routes.materialParticipationRoutes
import eu.tutorials.server.routes.userRoutes
import eu.tutorials.server.security.Base64PhotoEncoder
import eu.tutorials.server.security.BcryptPasswordHashed
import eu.tutorials.server.security.UserPrincipal
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.basic
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

fun Application.module() {
    DatabaseFactory.init()

    val binRepository = BinRepositoryImpl()
    val cardRepository = CardRepositoryImpl()
    val userRepository = UserRepositoryImpl()
    val helpRepository = HelpRepositoryImpl()
    val donationRepository = DonationRepositoryImpl()
    val materialParticipationRepository = MaterialParticipationRepositoryImpl()
    val historyRepository = HistoryRepositoryImpl()

    val passwordHashed: IPasswordHashed = BcryptPasswordHashed()
    val photoEncoder: IPhotoEncoder = Base64PhotoEncoder()

    val binController = BinController(
        createBin = CreateBinUseCase(binRepository),
        findBinById = FindBinByIdUseCase(binRepository),
        getAllBins = GetAllBinsUseCase(binRepository),
        deleteBin = DeleteBinUseCase(binRepository),
        filterBinsByRecipientName = FilterBinsByRecipientNameUseCase(
            iBinRepository = binRepository,
            iUserRepository = userRepository
        ),
        unblockedUser = UnblockedUserUseCase(binRepository)
    )

    val cardController = CardController(
        createCard = CreateCardUseCase(cardRepository),
        findCardById = FindCardByIdUseCase(cardRepository),
        getAllCards = GetAllCardsUseCase(cardRepository),
        updateCard = UpdateCardUseCase(cardRepository),
        deleteCard = DeleteCardUseCase(cardRepository)
    )

    val userController = UserController(
        createUserUseCase = CreateUserUseCase(userRepository, passwordHashed),
        authenticateUserUseCase = GetPasswordHashUseCase(userRepository, passwordHashed),
        findUserByIdUseCase = FindUserByIdUseCase(userRepository),
        getAllUsersUseCase = GetAllUsersUseCase(userRepository),
        updateUserUseCase = UpdateUserUseCase(userRepository, passwordHashed, photoEncoder),
        deleteUserUseCase = DeleteUserUseCase(userRepository),
        findUserByEmailUseCase = FindUserByEmailUseCase(userRepository),
        updateUserBlockUseCase = UpdateUserBlockUseCase(userRepository)
    )

    val helpController = HelpController(
        createHelp = CreateHelpUseCase(helpRepository),
        findHelpById = FindHelpByIdUseCase(helpRepository),
        getAllHelps = GetAllHelpsUseCase(helpRepository),
        deleteHelp = DeleteHelpUseCase(helpRepository),
        updateHelp = UpdateHelpUseCase(helpRepository, historyRepository)
    )

    val donationController = DonationController(
        createDonation = CreateDonationUseCase(donationRepository),
        findDonationById = FindDonationByIdUseCase(donationRepository),
        getAllDonations = GetAllDonationsUseCase(donationRepository),
        deleteDonation = DeleteDonationUseCase(donationRepository)
    )

    val materialParticipationController = MaterialParticipationController(
        createMaterialParticipation = CreateMaterialParticipationUseCase(materialParticipationRepository),
        findMaterialParticipationById = FindMaterialParticipationByIdUseCase(materialParticipationRepository, helpRepository),
        getAllMaterialParticipation = GetAllMaterialParticipationUseCase(materialParticipationRepository),
        updateMaterialParticipation = UpdateMaterialParticipationUseCase(materialParticipationRepository),
        deleteMaterialParticipation = DeleteMaterialParticipationUseCase(materialParticipationRepository)
    )

    val helpModule = SerializersModule {
        polymorphic(Help::class) {
            subclass(Help.Financial::class, Help.Financial.serializer())
            subclass(Help.Material::class, Help.Material.serializer())
        }
    }

    val historyController = HistoryController(
        createHistory = CreateHistoryUseCase(historyRepository),
        findHistoryById = FindHistoryByIdUseCase(historyRepository),
        getAllHistories = GetAllHistoriesUseCase(historyRepository),
        deleteHistory = DeleteHistoryUseCase(historyRepository, binRepository, userRepository, helpRepository),
        filterHistoriesByRecipientFullName = FilterHistoriesByRecipientFullNameUseCase(historyRepository, helpRepository, userRepository)
    )

    install(ContentNegotiation) {
        json(Json {
            serializersModule = helpModule
            classDiscriminator = "type"
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(Authentication) {
        basic(name = "auth-basic") {
            validate { credentials ->
                val userEntity: UserEntity? = userRepository.findEntityByEmail(credentials.name)

                if (userEntity != null && passwordHashed.verify(credentials.password, userEntity.password_hash)) {
                    UserPrincipal(userEntity.entityToDomain())
                } else null
            }
        }
    }


    routing {
        get("/") { call.respondText("Server is running!") }
        fondyRoutes()
        donationRoutes(donationController)
        binRoutes(binController)
        cardRoutes(cardController)
        userRoutes(userController)
        helpRoutes(helpController)
        materialParticipationRoutes(materialParticipationController)
        historyRoutes(historyController)
    }
}