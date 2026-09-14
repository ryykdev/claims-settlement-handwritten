package dev.ryyk.claims.decide

import java.time.LocalDateTime

data class DecisionEntity(
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val contract: String,
    val decision_status: DecisionStatus,
)


enum class DecisionStatus {
    APPROVED,
    REJECTED,
    MANUAL_REVIEW,
    UNKNOWN,
}