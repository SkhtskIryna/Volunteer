package eu.tutorials.volunteerapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import eu.tutorials.volunteerapp.ui.theme.Colors

@Composable
fun outlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    width: Dp
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                color = Color(Colors.DarkBlue.rgb)
            )
        },
        modifier = Modifier
            .width(width)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color(Colors.LightBlue.rgb),
            unfocusedIndicatorColor = Color.Gray,
            cursorColor = Color(Colors.DarkBlue.rgb),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}