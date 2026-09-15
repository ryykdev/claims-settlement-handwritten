package dev.ryyk.claims.decision

import dev.ryyk.claims.claim.ClaimEntity
import dev.ryyk.claims.contract.ContractEntity
import dev.ryyk.claims.contract.ContractStatus
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class DecisionRules {
    // this call requires that all arguments are not-null
    fun processClaim(
        claim: ClaimEntity,
        contract: ContractEntity?,
        associatedClaims: List<ClaimEntity>
    ): DecisionEntity {
        // REJECT if contract null
        if (contract == null) return DecisionEntity(
            id = claim.externalId,
            datetime = LocalDateTime.now(),
            status = DecisionStatus.REJECTED,
            payout = BigDecimal.ZERO,
            reason = DecisionReason.ORPHAN_CLAIM,
        )
        // REJECT if contract status not active
        if (contract.status != ContractStatus.ACTIVE) return DecisionEntity(
            id = claim.externalId,
            datetime = LocalDateTime.now(),
            status = DecisionStatus.REJECTED,
            payout = BigDecimal.ZERO,
            reason = DecisionReason.CONTRACT_NOT_ACTIVE,
        )

        // REJECT if claim or contract dates are implausible
        val validStartDate = LocalDate.of(2000, 1, 1)
        val validEndDate = LocalDate.now().plusYears(10)
        val contractStart = contract.startLeasing
        val contractEnd = contract.endLeasing
        val incidentDate = claim.incidentDate
        if (contractEnd == null || incidentDate == null)
            return DecisionEntity(
                id = claim.externalId,
                datetime = LocalDateTime.now(),
                status = DecisionStatus.MANUAL_REVIEW,
                payout = BigDecimal.ZERO,
                reason = DecisionReason.IMPLAUSIBLE_DATE,
            )

        if (contractStart.isBefore(validStartDate)
            || contractEnd.isAfter(validEndDate)
            || claim.incidentDate.isBefore(validStartDate)
            || claim.incidentDate.isAfter(validEndDate))
            return DecisionEntity(
                id = claim.externalId,
                datetime = LocalDateTime.now(),
                status = DecisionStatus.MANUAL_REVIEW,
                payout = BigDecimal.ZERO,
                reason = DecisionReason.IMPLAUSIBLE_DATE,
            )

        // REJECT if incident is not within leasing term
        if (claim.incidentDate.isBefore(contractStart)
            || claim.incidentDate.isAfter(contractEnd))
            return DecisionEntity(
                id = claim.externalId,
                datetime = LocalDateTime.now(),
                status = DecisionStatus.REJECTED,
                payout = BigDecimal.ZERO,
                reason = DecisionReason.INCIDENT_OUTSIDE_LEASE,
            )

        // REJECT if claim is not in decidable state
        if (claim.claimState.isFinal)
            return DecisionEntity(
                id = claim.externalId,
                datetime = LocalDateTime.now(),
                status = DecisionStatus.REJECTED,
                payout = BigDecimal.ZERO,
                reason = DecisionReason.CLAIM_STATE_FINAL,
            )

        // APPROVED
        return TODO()
    }
}