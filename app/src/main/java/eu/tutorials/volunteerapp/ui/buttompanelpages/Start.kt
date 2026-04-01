package eu.tutorials.volunteerapp.ui.buttompanelpages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import eu.tutorials.volunteerapp.MainViewModel

@Composable
fun Start(
    viewModel: MainViewModel,
    navHostController: NavHostController,
    bottomBar: @Composable () -> Unit,
    topBar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Scaffold(
        bottomBar = bottomBar,
        topBar = topBar,
        drawerGesturesEnabled = false
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp)
        ) {
            content()
        }
    }
}

