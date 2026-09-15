package dev.ryyk.claims.decision

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("decision")
data class DecisionEntity(
    @Id val id: Long, // claim.external_id
    val datetime: LocalDateTime = LocalDateTime.now(),
    val status: DecisionStatus,
    val payout: BigDecimal = BigDecimal.ZERO,
    val reason: DecisionReason,
)

enum class DecisionStatus {
    APPROVED,
    REJECTED,
    MANUAL_REVIEW,
}

enum class DecisionReason {
    NONE,
    CONTRACT_NOT_ACTIVE,
    ORPHAN_CLAIM,
    IMPLAUSIBLE_DATE,
    INCIDENT_OUTSIDE_LEASE,
    CLAIM_STATE_FINAL,
}