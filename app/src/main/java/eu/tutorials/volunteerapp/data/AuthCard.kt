package eu.tutorials.volunteerapp.data

import eu.tutorials.domain.utils.YearMonthSerializer
import kotlinx.serialization.Serializable
import java.time.YearMonth

data class AuthCard(
    val number: String,
    @Serializable(with = YearMonthSerializer::class)
    val validityPeriod: YearMonth,
    val cvv2: Int
)