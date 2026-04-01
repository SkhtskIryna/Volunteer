package eu.tutorials.volunteerapp.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.ui.navigation.Navigation

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(viewModel: MainViewModel) {
    val scaffoldState = rememberScaffoldState()
    val navHostController = rememberNavController()

    Scaffold(
        scaffoldState = scaffoldState,
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.White
    ) { innerPadding ->
        Navigation(
            navHostController = navHostController,
            viewModel = viewModel,
            pd = innerPadding
        )
    }
}
