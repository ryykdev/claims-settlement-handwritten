package dev.ryyk.claims.ingestion

import dev.ryyk.claims.claim.ClaimType
import org.springframework.core.io.ClassPathResource
import java.math.BigDecimal
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.asserter

class ClaimXmlParserTest {
    private val parser = ClaimXmlParser()
    @Test
    fun`parse claims_batch_1 xml successfully`() {
        val claims = ClassPathResource("data/claims_batch_1.xml")
            .inputStream.use { parser.parse(it) }
        assertEquals(16, claims.size)
        val first = claims.first()
        assertEquals(778120L, first.externalId)
        assertEquals(ClaimType.THEFT, first.claimType)
        assertEquals("KAU-003945", first.saleOrderNumber)
        assertEquals(BigDecimal.ZERO, first.repairCost)  // "False" → 0
    }
    @Test
    fun `parse orphan clame without sale_order_number using null`() {
        val claims = ClassPathResource("data/claims_batch_1.xml")
            .inputStream.use { parser.parse(it) }
        assertEquals(16, claims.size)
        val orphan = claims.first { it.externalId == 782030L }
        assertEquals(782030L, orphan.externalId)
        assertEquals(ClaimType.THEFT, orphan.claimType)
        assertEquals("KAU-999999", orphan.saleOrderNumber)
        assertEquals(BigDecimal.ZERO, orphan.repairCost)  // "False" → 0
    }
}