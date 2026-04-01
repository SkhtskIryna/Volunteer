package eu.tutorials.volunteerapp.ui.components

import android.content.Context
import java.io.File
import java.io.FileOutputStream

// Отримати файл для зберігання CVV2
fun getCvv2File(context: Context, userId: Int): File {
    val dir = File(context.filesDir, "cards")
    if (!dir.exists()) dir.mkdir()
    return File(dir, "cvv_$userId.txt")
}

// Зберегти CVV2 у файл
fun saveCvv2ToFile(context: Context, userId: Int, cvv2: String) {
    val file = getCvv2File(context, userId)
    FileOutputStream(file).use { out ->
        out.write(cvv2.toByteArray())
    }
}