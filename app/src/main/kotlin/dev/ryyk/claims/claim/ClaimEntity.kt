package dev.ryyk.claims.claim

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Table("claim")
data class ClaimEntity(
    val updated: LocalDateTime,
    @Id @Column("id") val externalId: Long = 0,
    @Column("claim_type") val claimType: ClaimType,
    @Column("claim_state") val claimState: ClaimState,
    @Column("sale_order_number") val saleOrderNumber: String?, // foreign key from contract
    @Column("incident_date") val incidentDate: LocalDate?, // claim requires an incident date
    @Column("reported_date") val reportedDate: LocalDate?,
    @Column("repair_cost") val repairCost: BigDecimal?,
    @Column("police_report_number") val policeReportNumber: String,
    val description: String,
) : Persistable<Long> {
    @Transient
    private var isNewEntity: Boolean = true
    override fun getId(): Long = externalId
    override fun isNew(): Boolean = isNewEntity

    // for updating existing rows
    fun markExisting() {
        isNewEntity = false
    }
}

enum class ClaimType {
    THEFT,
    PARTIAL_DAMAGE,
    TOTAL_DAMAGE,
    PAYMENT_DEFAULT,
    UNKNOWN;

    companion object {
        fun fromRaw(value: String?): ClaimType {
            if (value.isNullOrBlank() || value == "False") return UNKNOWN
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
        }
    }

}

enum class ClaimState {
    INVESTIGATION,
    AWAITING_DOCUMENTS,
    SETTLED,
    NOT_SETTLED,
    INVALID;
    val isFinal: Boolean
        get() = when (this) {
            INVESTIGATION, AWAITING_DOCUMENTS -> false
            SETTLED, NOT_SETTLED, INVALID -> true
        }
    companion object {
        fun fromRaw(value: String?): ClaimState {
            val normalized = value
                ?.trim()
                ?.takeUnless { it.isBlank() || it.equals("False", ignoreCase = true)  }
            return when (normalized) {
                "Schaden gemeldet / prüfen" -> INVESTIGATION
                "Unterlagen angefordert" -> AWAITING_DOCUMENTS
                "reguliert", "bezahlt" -> SETTLED
                "abgelehnt" -> NOT_SETTLED
                "gegenstandslos" -> INVALID
                else -> INVALID
            }
        }
    }

}
