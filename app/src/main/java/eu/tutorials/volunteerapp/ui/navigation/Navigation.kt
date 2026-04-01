package eu.tutorials.volunteerapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.ui.buttompanelpages.Home
import eu.tutorials.volunteerapp.ui.buttompanelpages.Start
import eu.tutorials.volunteerapp.ui.components.AdminBottomBar
import eu.tutorials.volunteerapp.ui.components.Line
import eu.tutorials.volunteerapp.ui.components.RecipientBottomBar
import eu.tutorials.volunteerapp.ui.components.TopBar
import eu.tutorials.volunteerapp.ui.components.VolunteerBottomBar
import eu.tutorials.volunteerapp.ui.contents.admin.BlockedContent
import eu.tutorials.volunteerapp.ui.contents.admin.HelpDetailsForAdmin
import eu.tutorials.volunteerapp.ui.contents.admin.HelpRequestsContent
import eu.tutorials.volunteerapp.ui.contents.admin.HistoryContent
import eu.tutorials.volunteerapp.ui.contents.recipient.CreateRequestsContent
import eu.tutorials.volunteerapp.ui.contents.recipient.EditHelp
import eu.tutorials.volunteerapp.ui.contents.recipient.FinancialRecipientContent
import eu.tutorials.volunteerapp.ui.contents.recipient.HelpDetails
import eu.tutorials.volunteerapp.ui.contents.recipient.MaterialRecipientContent
import eu.tutorials.volunteerapp.ui.contents.volunteer.FinancialDetailsForVolunteer
import eu.tutorials.volunteerapp.ui.contents.volunteer.FinancialHelpDetailsForVolunteer
import eu.tutorials.volunteerapp.ui.contents.volunteer.FinancialVolunteerContent
import eu.tutorials.volunteerapp.ui.contents.volunteer.HelpVolunteerContent
import eu.tutorials.volunteerapp.ui.contents.volunteer.MaterialDetailsForVolunteer
import eu.tutorials.volunteerapp.ui.contents.volunteer.MaterialHelpDetailsForVolunteer
import eu.tutorials.volunteerapp.ui.contents.volunteer.MaterialVolunteerContent
import eu.tutorials.volunteerapp.ui.profile.CardSettings
import eu.tutorials.volunteerapp.ui.profile.Profile
import eu.tutorials.volunteerapp.ui.profile.ProfileSettings
import eu.tutorials.volunteerapp.ui.startpages.Administrators
import eu.tutorials.volunteerapp.ui.startpages.SignIn
import eu.tutorials.volunteerapp.ui.startpages.SignUp

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(navHostController: NavHostController, viewModel: MainViewModel, pd: PaddingValues) {
    NavHost(
        navController = navHostController,
        startDestination = "sign_up"
    ) {
        composable("sign_up") {
            Start(
                viewModel = viewModel,
                navHostController = navHostController,
                bottomBar = { Line() },
                topBar = { Line() }
            ) {
                SignUp(viewModel = viewModel, navController = navHostController)
            }
        }
        composable("sign_in") {
            Start(
                viewModel = viewModel,
                navHostController = navHostController,
                bottomBar = { Line() },
                topBar = { Line() }
            ) {
                SignIn(viewModel = viewModel, navController = navHostController)
            }
        }
        composable("administrators") {
            Start(
                viewModel = viewModel,
                navHostController = navHostController,
                bottomBar = { Line() },
                topBar = { Line() }
            ) {
                Administrators(viewModel = viewModel, navController = navHostController)
            }
        }

        composable(
            route = "profile/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val cardId = backStackEntry.arguments?.getInt("cardId") ?: 0
            Start(
                viewModel = viewModel,
                navHostController = navHostController,
                bottomBar = { Line() },
                topBar = { Line() }
            ) {
                Profile(
                    viewModel = viewModel,
                    navController = navHostController,
                    userId = userId,
                    cardId = cardId
                )
            }
        }

        composable(
            route = "profile_settings/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Start(
                viewModel = viewModel,
                navHostController = navHostController,
                bottomBar = { Line() },
                topBar = { Line() }
            ) {
                ProfileSettings(
                    viewModel = viewModel,
                    navController = navHostController,
                    userId = userId
                )
            }
        }

        composable(
            route = "card_settings/{cardId}",
            arguments = listOf(navArgument("cardId") { type = NavType.IntType })
        ) { backStackEntry ->
            val cardId = backStackEntry.arguments?.getInt("cardId") ?: 0
            Start(
                viewModel = viewModel,
                navHostController = navHostController,
                bottomBar = { Line() },
                topBar = { Line() }
            ) {
                CardSettings(
                    viewModel = viewModel,
                    navController = navHostController
                )
            }
        }

        composable("material_recipient/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })) {
                backStackEntry ->
            val scaffoldState = rememberScaffoldState()
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Home(
                viewModel = viewModel,
                bottomBar = { RecipientBottomBar(navHostController, viewModel) },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                MaterialRecipientContent(viewModel, navHostController, userId)
            }
        }

        composable("financial_recipient/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })) {
                backStackEntry ->
            val scaffoldState = rememberScaffoldState()
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Home(
                viewModel = viewModel,
                bottomBar = { RecipientBottomBar(navHostController, viewModel) },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                FinancialRecipientContent(viewModel, navHostController, userId)
            }
        }

        composable("create_requests/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })) {
                backStackEntry ->
            val scaffoldState = rememberScaffoldState()
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Home(
                viewModel = viewModel,
                bottomBar = { RecipientBottomBar(navHostController, viewModel) },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                CreateRequestsContent(viewModel, navHostController, userId)
            }
        }

        composable(
            route = "help_details/{helpId}",
            arguments = listOf(navArgument("helpId") { type = NavType.IntType })
        ) { backStackEntry ->
            val helpId = backStackEntry.arguments?.getInt("helpId") ?: 0
            val scaffoldState = rememberScaffoldState()
            Home(
                viewModel = viewModel,
                bottomBar = { Line() },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                HelpDetails(
                    helpId = helpId,
                    viewModel = viewModel,
                    navController = navHostController
                )
            }
        }

        composable(
            route = "help_details_for_admin/{helpId}",
            arguments = listOf(navArgument("helpId") { type = NavType.IntType })
        ) { backStackEntry ->
            val helpId = backStackEntry.arguments?.getInt("helpId") ?: 0
            val scaffoldState = rememberScaffoldState()
            Home(
                viewModel = viewModel,
                bottomBar = { Line() },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                HelpDetailsForAdmin(
                    helpId = helpId,
                    viewModel = viewModel,
                    navController = navHostController
                )
            }
        }

        composable(
            route = "financial_help_details_for_volunteer/{helpId}",
            arguments = listOf(navArgument("helpId") { type = NavType.IntType })
        ) { backStackEntry ->
            val helpId = backStackEntry.arguments?.getInt("helpId") ?: 0
            val scaffoldState = rememberScaffoldState()
            Home(
                viewModel = viewModel,
                bottomBar = { Line() },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                FinancialHelpDetailsForVolunteer(
                    helpId = helpId,
                    viewModel = viewModel,
                    navController = navHostController
                )
            }
        }

        composable(
            route = "material_help_details_for_volunteer/{helpId}",
            arguments = listOf(navArgument("helpId") { type = NavType.IntType })
        ) { backStackEntry ->
            val helpId = backStackEntry.arguments?.getInt("helpId") ?: 0
            val scaffoldState = rememberScaffoldState()
            Home(
                viewModel = viewModel,
                bottomBar = { Line() },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                MaterialHelpDetailsForVolunteer(
                    helpId = helpId,
                    viewModel = viewModel,
                    navController = navHostController
                )
            }
        }

        composable(
            route = "material_details_for_volunteer/{helpId}",
            arguments = listOf(navArgument("helpId") { type = NavType.IntType })
        ) { backStackEntry ->
            val helpId = backStackEntry.arguments?.getInt("helpId") ?: 0
            val scaffoldState = rememberScaffoldState()
            Home(
                viewModel = viewModel,
                bottomBar = { Line() },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                MaterialDetailsForVolunteer(
                    helpId = helpId,
                    viewModel = viewModel,
                    navController = navHostController
                )
            }
        }

        composable(
            route = "financial_details_for_volunteer/{helpId}/{donationId}",
            arguments = listOf(
                navArgument("helpId") { type = NavType.IntType },
                navArgument("donationId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val helpId = backStackEntry.arguments?.getInt("helpId") ?: 0
            val donationId = backStackEntry.arguments?.getInt("donationId") ?: 0
            val scaffoldState = rememberScaffoldState()
            Home(
                viewModel = viewModel,
                bottomBar = { Line() },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                FinancialDetailsForVolunteer(
                    helpId = helpId,
                    donationId = donationId,
                    viewModel = viewModel,
                    navController = navHostController
                )
            }
        }

        composable(
            route = "help_edit/{helpId}",
            arguments = listOf(navArgument("helpId") { type = NavType.IntType })
        ) { backStackEntry ->
            val helpId = backStackEntry.arguments?.getInt("helpId") ?: 0
            val scaffoldState = rememberScaffoldState()
            Home(
                viewModel = viewModel,
                bottomBar = { Line() },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                EditHelp(
                    helpId = helpId,
                    viewModel = viewModel,
                    navController = navHostController
                )
            }
        }

        composable("history/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })) {
                backStackEntry ->
            val scaffoldState = rememberScaffoldState()
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Home(
                viewModel = viewModel,
                bottomBar = { AdminBottomBar(navHostController, viewModel) },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                HistoryContent(
                    viewModel = viewModel,
                    navController = navHostController,
                    userId = userId
                )
            }
        }

        composable("requests_for_help/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })) {
                backStackEntry ->
            val scaffoldState = rememberScaffoldState()
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Home(
                viewModel = viewModel,
                bottomBar = { AdminBottomBar(navHostController, viewModel) },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                HelpRequestsContent(viewModel, navHostController, userId)
            }
        }

        composable("blocked/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })) {
                backStackEntry ->
            val scaffoldState = rememberScaffoldState()
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Home(
                viewModel = viewModel,
                bottomBar = { AdminBottomBar(navHostController, viewModel) },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                BlockedContent(viewModel, navHostController, userId)
            }
        }

        composable("financial_volunteer/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })) {
                backStackEntry ->
            val scaffoldState = rememberScaffoldState()
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Home(
                viewModel = viewModel,
                bottomBar = { VolunteerBottomBar(navHostController, viewModel) },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                FinancialVolunteerContent(viewModel, navHostController, userId)
            }
        }

        composable("material_volunteer/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })) {
                backStackEntry ->
            val scaffoldState = rememberScaffoldState()
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Home(
                viewModel = viewModel,
                bottomBar = { VolunteerBottomBar(navHostController, viewModel) },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                MaterialVolunteerContent(viewModel, navHostController, userId)
            }
        }

        composable("my_help/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.IntType })) {
                backStackEntry ->
            val scaffoldState = rememberScaffoldState()
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            Home(
                viewModel = viewModel,
                bottomBar = { VolunteerBottomBar(navHostController, viewModel) },
                topBar = { TopBar(navHostController, viewModel) },
                scaffoldState = scaffoldState
            ) {
                HelpVolunteerContent(viewModel, navHostController, userId)
            }
        }
    }
}