package eu.tutorials.volunteerapp.ui.navigation.signUp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import eu.tutorials.volunteerapp.ui.theme.Colors

@Composable
fun LinkToAdministrators(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val annotatedText = buildAnnotatedString {
            pushStringAnnotation(tag = "Administrators", annotation = "administrators")
            withStyle(
                style = SpanStyle(
                    color = Color(Colors.DarkBlue.rgb),
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            ) {
                append("Адміністратори")
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 16.sp,
                color = Color(Colors.DarkBlue.rgb),
                fontWeight = FontWeight.Normal
            ),
            onClick = { offset ->
                annotatedText.getStringAnnotations(tag = "Administrators", start = offset, end = offset)
                    .firstOrNull()?.let {
                        navController.navigate("administrators")
                    }
            }
        )
    }
}