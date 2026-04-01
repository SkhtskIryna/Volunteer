package eu.tutorials.volunteerapp.ui.buttompanelpages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import eu.tutorials.volunteerapp.MainViewModel

@Composable
fun Home(
    viewModel: MainViewModel,
    bottomBar: @Composable () -> Unit,
    topBar: @Composable () -> Unit,
    scaffoldState: ScaffoldState,
    content: @Composable () -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = topBar,
        bottomBar = bottomBar,
        modifier = Modifier.fillMaxSize(),
        drawerGesturesEnabled = false
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            content()
        }
    }
}




