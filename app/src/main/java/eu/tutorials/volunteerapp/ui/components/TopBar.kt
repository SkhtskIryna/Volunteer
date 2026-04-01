package eu.tutorials.volunteerapp.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import eu.tutorials.volunteerapp.MainViewModel
import eu.tutorials.volunteerapp.data.User
import eu.tutorials.volunteerapp.ui.theme.Colors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    viewModel: MainViewModel
) {
    // Поточний ID користувача
    val currentUserId by viewModel.currentUserId.collectAsState(initial = null)

    // Локальний стан користувача (щоб показати ініціали)
    var user by remember { mutableStateOf<User?>(null) }

    // Завантаження користувача за поточним ID
    LaunchedEffect(currentUserId) {
        currentUserId?.let { id ->
            viewModel.getUserById(id) { u ->
                user = u
            }
        }
    }

    val avatarBitmap = remember(user?.photoBase64) {
        user?.photoBase64?.takeIf { it.isNotEmpty() }?.let { base64 ->
            try {
                val decoded = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                BitmapFactory.decodeByteArray(decoded, 0, decoded.size).asImageBitmap()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    // Завантаження користувача при зміні ID
    LaunchedEffect(currentUserId) {
        val id = currentUserId
        if (id != null && id != 0) {
            viewModel.getUserById(id) {
                user = it
            }
        } else {
            user = null
        }
    }

    TopAppBar(
        title = {},
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(Colors.MainBlue.rgb)
        ),
        actions = {
            IconButton(
                onClick = {
                    val userId = currentUserId ?: 0
                    navController.navigate("profile/$userId")
                }
            ) {
                if (user != null) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(Color(Colors.MainBlue.rgb)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatarBitmap != null) {
                            Image(
                                bitmap = avatarBitmap,
                                contentDescription = "Аватар",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            androidx.compose.material.Text(
                                text = getInitials("${user?.firstName ?: ""} ${user?.lastName ?: ""}"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Профіль користувача",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview(){
    val fakeNavController = rememberNavController()
    val fakeViewModel : MainViewModel = viewModel()
    TopBar(
        navController = fakeNavController,
        viewModel = fakeViewModel
    )
}