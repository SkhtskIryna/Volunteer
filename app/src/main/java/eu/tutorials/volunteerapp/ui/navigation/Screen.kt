package eu.tutorials.volunteerapp.ui.navigation

import androidx.annotation.DrawableRes
import eu.tutorials.volunteerapp.R

sealed class Screen(
    val title: String,
    val route: String,
    @DrawableRes val icon: Int
) {

    sealed class Admin(
        title: String,
        route: String,
        icon: Int
    ) : Screen(title, route, icon) {

        object History : Admin("Історія", "history", R.drawable.ic_history)
        object Requests : Admin("Запити на допомогу", "requests_for_help", R.drawable.ic_home)
        object Blocked : Admin("Заблоковані", "blocked", R.drawable.ic_blocked)
    }

    sealed class Recipient(
        title: String,
        route: String,
        icon: Int
    ) : Screen(title, route, icon) {

        object Material : Recipient("Матеріальна", "material_recipient", R.drawable.ic_material)
        object Financial : Recipient("Фінансова", "financial_recipient", R.drawable.ic_financial)
        object MyRequests : Recipient("Мої запити", "create_requests", R.drawable.ic_records)
    }

    sealed class Volunteer(
        title: String,
        route: String,
        icon: Int
    ) : Screen(title, route, icon) {

        object Material : Volunteer("Матеріальна", "material_volunteer", R.drawable.ic_material)
        object Financial : Volunteer("Фінансова", "financial_volunteer", R.drawable.ic_financial)
        object MyHelp : Volunteer("Моя допомога", "my_help", R.drawable.ic_records)
    }
}

val screensInBottomAdmin = listOf(
    Screen.Admin.History,
    Screen.Admin.Requests,
    Screen.Admin.Blocked
)

val screensInBottomRecipient = listOf(
    Screen.Recipient.Material,
    Screen.Recipient.Financial,
    Screen.Recipient.MyRequests
)

val screensInBottomVolunteer = listOf(
    Screen.Volunteer.Material,
    Screen.Volunteer.Financial,
    Screen.Volunteer.MyHelp
)

