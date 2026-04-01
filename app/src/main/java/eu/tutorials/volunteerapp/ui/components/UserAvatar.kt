package eu.tutorials.volunteerapp.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.tutorials.volunteerapp.ui.theme.Colors

@Composable
fun UserAvatar(
    photoBase64: String?,
    firstName: String,
    lastName: String,
    size: Dp = 36.dp
) {
    val avatarBitmap = remember(photoBase64) {
        photoBase64
            ?.takeIf { it.isNotBlank() }
            ?.let {
                try {
                    val decoded = Base64.decode(it, Base64.DEFAULT)
                    BitmapFactory
                        .decodeByteArray(decoded, 0, decoded.size)
                        ?.asImageBitmap()
                } catch (e: Exception) {
                    null
                }
            }
    }

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(Colors.MainBlue.rgb)),
        contentAlignment = Alignment.Center
    ) {
        if (avatarBitmap != null) {
            Image(
                bitmap = avatarBitmap,
                contentDescription = "Аватар",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = getInitials("$firstName $lastName"),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value / 2).sp
            )
        }
    }
}
