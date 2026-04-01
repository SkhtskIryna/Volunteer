package eu.tutorials.volunteerapp.ui.components

import android.content.Context
import java.io.File
import java.io.FileOutputStream

fun getCardNumberFile(context: Context, userId: Int): File {
    val dir = File(context.filesDir, "cards")
    if (!dir.exists()) dir.mkdir()
    return File(dir, "number_$userId.txt")
}

fun saveCardNumberToFile(context: Context, userId: Int, number: String) {
    val file = getCardNumberFile(context, userId)
    FileOutputStream(file).use { out -> out.write(number.toByteArray()) }
}

fun loadCardNumberFromFile(context: Context, userId: Int): String? {
    val file = getCardNumberFile(context, userId)
    return if (file.exists()) file.readText() else null
}