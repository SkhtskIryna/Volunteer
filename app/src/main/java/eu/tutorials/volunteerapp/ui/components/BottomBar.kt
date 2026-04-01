package eu.tutorials.volunteerapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.ui.navigation.Screen
import eu.tutorials.volunteerapp.ui.navigation.screensInBottomAdmin
import eu.tutorials.volunteerapp.ui.navigation.screensInBottomRecipient
import eu.tutorials.volunteerapp.ui.navigation.screensInBottomVolunteer
import eu.tutorials.volunteerapp.ui.theme.Colors

@Composable
fun BottomBar(
    navController: NavHostController,
    screens: List<Screen>,
    userId: Int?
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry
        ?.destination
        ?.route
        ?.substringBefore("/")

    NavigationBar(
        modifier = Modifier.height(140.dp),
        containerColor = Color(Colors.MainBlue.rgb)
    ) {
        screens.forEach { screen ->

            val navigateRoute = "${screen.route}/${userId ?: 0}"

            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(navigateRoute) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .width(72.dp)
                            .fillMaxHeight()
                    ) {
                        Icon(
                            painter = painterResource(screen.icon),
                            contentDescription = screen.title,
                            tint = Color(Colors.LightBlue.rgb),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = screen.title,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            color = Color(Colors.LightBlue.rgb)
                        )
                    }
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent,
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White
                )
            )
        }
    }
}

@Composable
fun RecipientBottomBar(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val userId by mainViewModel.currentUserId.collectAsState()

    BottomBar(
        navController = navController,
        screens = screensInBottomRecipient,
        userId = userId
    )
}

@Composable
fun AdminBottomBar(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val userId by mainViewModel.currentUserId.collectAsState()

    BottomBar(
        navController = navController,
        screens = screensInBottomAdmin,
        userId = userId
    )
}

@Composable
fun VolunteerBottomBar(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val userId by mainViewModel.currentUserId.collectAsState()

    BottomBar(
        navController = navController,
        screens = screensInBottomVolunteer,
        userId = userId
    )
}
