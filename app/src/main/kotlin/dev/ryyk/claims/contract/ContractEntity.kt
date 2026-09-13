package dev.ryyk.claims.contract

import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import org.springframework.data.annotation.Transient

@Table("contract")
data class ContractEntity(
    val updated: LocalDateTime,
    @Id val name: String,
    @Column("sale_order_number") val saleOrderNumber: String,
    @Column("partner_id") val partnerId: Int = 0,
    @Column("partner_name") val partnerName: String,
    @Column("customer_company_id") val customerCompanyId: Int = 0,
    @Column("customer_company_name") val customerCompanyName: String,
    @Column("frame_number") val frameNumber: String,
    @Column("brand") val brand: String,
    @Column("model") val model: String,
    @Column("net_value")  val netValue: BigDecimal,
    @Column("start_leasing") val startLeasing: LocalDate,
    @Column("end_leasing") val endLeasing: LocalDate?,
    @Column("term") val term: Int = 0,
    @Column("insurance_rate") val insuranceRate: BigDecimal,
    @Column("status") val status: Status,
    ): Persistable<String> {
        @Transient
        private var isNewEntity: Boolean = true
    override fun getId(): String = name
    override fun isNew(): Boolean = isNewEntity

    // for updating existing rows
    fun markExisting() {
        isNewEntity = false
    }
    }

enum class Status {
    ACTIVE,
    DONE,
    CANCEL,
    UNKNOWN;

    companion object {
        fun fromRaw(value: String?): Status {
            if (value.isNullOrBlank() || value == "False") return UNKNOWN
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
        }
    }
}