package eu.tutorials.volunteerapp.ui.components

fun getInitials(fullName: String): String {
    return fullName.split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
}

fun formatPhone(phone: String?): String {
    if (phone.isNullOrBlank()) return "Немає телефону"

    val digits = phone.filter { it.isDigit() }

    return when {
        digits.length >= 10 -> "(${digits.substring(0, 3)}) ${digits.substring(3, 6)}-${digits.substring(6, 8)}-${digits.substring(8, 10)}"
        else -> digits
    }
}