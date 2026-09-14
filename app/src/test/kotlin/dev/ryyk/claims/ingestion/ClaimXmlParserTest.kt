package dev.ryyk.claims.ingestion

import dev.ryyk.claims.claim.ClaimType
import dev.ryyk.claims.claim.ClaimState
import org.springframework.core.io.ClassPathResource
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals

class ClaimXmlParserTest {
    private val parser = ClaimXmlParser()

    @Test
    fun `parse 778120L of claims_batch_1 xml successfully`() {
        val claims = ClassPathResource("data/claims_batch_1.xml")
            .inputStream.use { parser.parse(it) }
        assertEquals(16, claims.size)
        val first = claims.first()
        assertEquals(778120L, first.externalId)
        assertEquals(ClaimType.THEFT, first.claimType)
        assertEquals(ClaimState.INVESTIGATION, first.claimState)
        assertEquals("KAU-003945", first.saleOrderNumber)
        assertEquals(LocalDate.of(2026, 3, 23), first.incidentDate)
        assertEquals(LocalDate.of(2026, 3, 25), first.reportedDate)
        assertEquals(BigDecimal.ZERO, first.repairCost)  // "False" → 0
        assertEquals("Fahrrad aus Tiefgarage entwendet, Schloss aufgebrochen", first.description)
    }

    @Test
    fun `parse orphan clame without sale_order_number`() {
        val claims = ClassPathResource("data/claims_batch_1.xml")
            .inputStream.use { parser.parse(it) }
        assertEquals(16, claims.size)
        val orphan = claims.first { it.externalId == 782030L }
        assertEquals(782030L, orphan.externalId)
        assertEquals(ClaimType.THEFT, orphan.claimType)
        assertEquals("KAU-999999", orphan.saleOrderNumber) // saved to db as null
        assertEquals(BigDecimal.ZERO, orphan.repairCost)  // "False" → 0
    }
}