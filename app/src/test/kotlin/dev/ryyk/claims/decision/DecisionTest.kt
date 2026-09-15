package dev.ryyk.claims.decision

import dev.ryyk.claims.claim.ClaimEntity
import dev.ryyk.claims.claim.ClaimState
import dev.ryyk.claims.claim.ClaimType
import dev.ryyk.claims.contract.ContractEntity
import dev.ryyk.claims.contract.ContractStatus
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

class DecisionTest {

    private val rules = DecisionRules()

    @Test
    fun `reject if claim is orphan - contract is null`() {
        val updated = LocalDateTime.now()
        val claim = ClaimEntity(
            updated = updated,
            externalId = 780110,
            claimType = ClaimType.PARTIAL_DAMAGE,
            claimState = ClaimState.INVESTIGATION,
            saleOrderNumber = "KAU-003945",
            incidentDate = LocalDate.of(2026, 4, 2),
            reportedDate = LocalDate.of(2026, 4, 4),
            policeReportNumber = "",
            repairCost = BigDecimal("412.50"),
            description = "Sturz auf Radweg, Schaltwerk und Laufrad beschädigt"
        )
        val contract = null

        val should = DecisionEntity(
            id = 780110,
            datetime = updated,
            status = DecisionStatus.REJECTED,
            payout = BigDecimal.ZERO,
            reason = DecisionReason.ORPHAN_CLAIM)

        val result = rules.processClaim(claim, contract, emptyList())

        assertEquals(should, result)
    }

    @Test
    fun `REJECT if contract status not active`() {
        val updated = LocalDateTime.now()
        val claim = ClaimEntity(
            updated = updated,
            externalId = 780110,
            claimType = ClaimType.PARTIAL_DAMAGE,
            claimState = ClaimState.INVESTIGATION,
            saleOrderNumber = "KAU-003945",
            incidentDate = LocalDate.of(2026, 4, 2),
            reportedDate = LocalDate.of(2026, 4, 4),
            policeReportNumber = "",
            repairCost = BigDecimal("412.50"),
            description = "Sturz auf Radweg, Schaltwerk und Laufrad beschädigt"
        )
        val contract = ContractEntity(
            updated = updated,
            name = "ELV-100377",
            saleOrderNumber = "KAU-004311",
            partnerId = 3001,
            partnerName = "René Böhm",
            customerCompanyId = 214,
            customerCompanyName = "Kanzlei Böttcher & Partner",
            frameNumber = "SPZ4412",
            brand = "Specialized",
            model = "Turbo Vado 4.0",
            netValue = BigDecimal(" 4650.00"),
            startLeasing = LocalDate.of(2023,5,1),
            endLeasing = LocalDate.of(2026,4,3),
            term = 36,
            insuranceRate = BigDecimal("5.80"),
            status = ContractStatus.DONE,
        )

        val should = DecisionEntity(
            id = 780110,
            datetime = updated,
            status = DecisionStatus.REJECTED,
            payout = BigDecimal.ZERO,
            reason = DecisionReason.ORPHAN_CLAIM)

        val result = rules.processClaim(claim, contract, emptyList())

        assertEquals(should, result)
    }

    @Test
    fun `REJECT if claim has implausible date`() {
        val updated = LocalDateTime.now()
        val claim = ClaimEntity(
            updated = updated,
            externalId = 780110,
            claimType = ClaimType.PARTIAL_DAMAGE,
            claimState = ClaimState.INVESTIGATION,
            saleOrderNumber = "KAU-003945",
            incidentDate = LocalDate.of(204, 4, 2),
            reportedDate = LocalDate.of(2026, 4, 4),
            policeReportNumber = "",
            repairCost = BigDecimal("412.50"),
            description = "Sturz auf Radweg, Schaltwerk und Laufrad beschädigt"
        )
        val contract = ContractEntity(
            updated = updated,
            name = "ELV-100377",
            saleOrderNumber = "KAU-004311",
            partnerId = 3001,
            partnerName = "René Böhm",
            customerCompanyId = 214,
            customerCompanyName = "Kanzlei Böttcher & Partner",
            frameNumber = "SPZ4412",
            brand = "Specialized",
            model = "Turbo Vado 4.0",
            netValue = BigDecimal(" 4650.00"),
            startLeasing = LocalDate.of(2023,5,1),
            endLeasing = LocalDate.of(2026,4,3),
            term = 36,
            insuranceRate = BigDecimal("5.80"),
            status = ContractStatus.DONE,
        )

        val should = DecisionEntity(
            id = 780110,
            datetime = updated,
            status = DecisionStatus.MANUAL_REVIEW,
            payout = BigDecimal.ZERO,
            reason = DecisionReason.ORPHAN_CLAIM)

        val result = rules.processClaim(claim, contract, emptyList())

        assertEquals(should, result)
    }

    @Test
    fun `REJECT if claim incident is not within leasing term`() {
        val updated = LocalDateTime.now()
        val claim = ClaimEntity(
            updated = updated,
            externalId = 780110,
            claimType = ClaimType.PARTIAL_DAMAGE,
            claimState = ClaimState.INVESTIGATION,
            saleOrderNumber = "KAU-003945",
            incidentDate = LocalDate.of(2022, 4, 2),
            reportedDate = LocalDate.of(2026, 4, 4),
            policeReportNumber = "",
            repairCost = BigDecimal("412.50"),
            description = "Sturz auf Radweg, Schaltwerk und Laufrad beschädigt"
        )
        val contract = ContractEntity(
            updated = updated,
            name = "ELV-100377",
            saleOrderNumber = "KAU-004311",
            partnerId = 3001,
            partnerName = "René Böhm",
            customerCompanyId = 214,
            customerCompanyName = "Kanzlei Böttcher & Partner",
            frameNumber = "SPZ4412",
            brand = "Specialized",
            model = "Turbo Vado 4.0",
            netValue = BigDecimal("4650.00"),
            startLeasing = LocalDate.of(2023,5,1),
            endLeasing = LocalDate.of(2026,4,3),
            term = 36,
            insuranceRate = BigDecimal("5.80"),
            status = ContractStatus.DONE,
        )

        val should = DecisionEntity(
            id = 780110,
            datetime = updated,
            status = DecisionStatus.REJECTED,
            payout = BigDecimal.ZERO,
            reason = DecisionReason.INCIDENT_OUTSIDE_LEASE)

        val result = rules.processClaim(claim, contract, emptyList())

        assertEquals(should, result)
    }

    @Test
    fun `REJECT if claim is not in decidable state`() {
        val updated = LocalDateTime.now()
        val claim = ClaimEntity(
            updated = updated,
            externalId = 785210,
            claimType = ClaimType.PARTIAL_DAMAGE,
            claimState = ClaimState.SETTLED,
            saleOrderNumber = "KAU-003945",
            incidentDate = LocalDate.of(2022, 11, 8),
            reportedDate = LocalDate.of(2025,11,10),
            policeReportNumber = "",
            repairCost = BigDecimal("89.90"),
            description = "Sturz auf Radweg, Schaltwerk und Laufrad beschädigt"
        )
        val contract = ContractEntity(
            updated = updated,
            name = "ELV-100377",
            saleOrderNumber = "KAU-004311",
            partnerId = 3001,
            partnerName = "René Böhm",
            customerCompanyId = 214,
            customerCompanyName = "Kanzlei Böttcher & Partner",
            frameNumber = "SPZ4412",
            brand = "Specialized",
            model = "Turbo Vado 4.0",
            netValue = BigDecimal("4650.00"),
            startLeasing = LocalDate.of(2023,5,1),
            endLeasing = LocalDate.of(2026,4,3),
            term = 36,
            insuranceRate = BigDecimal("5.80"),
            status = ContractStatus.DONE,
        )

        val should = DecisionEntity(
            id = 780110,
            datetime = updated,
            status = DecisionStatus.REJECTED,
            payout = BigDecimal.ZERO,
            reason = DecisionReason.INCIDENT_OUTSIDE_LEASE)

        val result = rules.processClaim(claim, contract, emptyList())

        assertEquals(should, result)
    }
}