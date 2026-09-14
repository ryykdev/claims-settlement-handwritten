package dev.ryyk.claims.ingestion

import dev.ryyk.claims.claim.ClaimEntity
import dev.ryyk.claims.claim.ClaimState
import dev.ryyk.claims.claim.ClaimType
import org.springframework.stereotype.Service
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ClaimXmlParser() {

    fun parse(inputStream: InputStream): List<ClaimEntity> {

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(inputStream)
        doc.documentElement.normalize()

        // get the export timestamp
        val rootElement = doc.documentElement // <export> element

        val model = rootElement.getAttribute("model")
        assert(model == "insurance.claim") // we need to know if it changes
        val source = rootElement.getAttribute("source")
        assert(source == "insurer_feed") // we need to know if it changes
        val generatedAt = rootElement.getAttribute("generated_at")

        val recordNodes = doc.getElementsByTagName("record")
        val claims = mutableListOf<ClaimEntity>()

        for (i in 0 until recordNodes.length) {
            val recordElement = recordNodes.item(i) as Element
            val fieldNodes = recordElement.getElementsByTagName("field")

            // read into map
            val fieldMap = mutableMapOf<String, String>()
            for (j in 0 until fieldNodes.length) {
                val fieldElement = fieldNodes.item(j) as Element
                val fieldName = fieldElement.getAttribute("name")
                val fieldValue = fieldElement.textContent.trim()
                fieldMap[fieldName] = fieldValue
            }

            // add updated
            fieldMap["updated"] = generatedAt
            claims.add(mapToClaim(fieldMap))
        }

        return claims
    }

    private fun mapToClaim(fields: Map<String, String>): ClaimEntity {
        // Parse tuple format: "[2841, 'Anna Müller']"
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        // using !! because if this fails we need fix it fast
        return ClaimEntity(
            updated = LocalDateTime.parse(fields["updated"]!!, dateTimeFormatter),
            externalId = fields["external_id"]!!.toLong(),
            claimType = ClaimType.fromRaw(fields["type"]),
            claimState = ClaimState.fromRaw(fields["state"]),
            saleOrderNumber = fields["sale_order_number"].orEmpty(),
            incidentDate = fields["incident_date"]
                ?.takeUnless { it.isBlank() || it.equals("False", ignoreCase = true) }
                ?.let { LocalDate.parse(it) }
            , // can be 'False'
            reportedDate = fields["reported_date"]
                ?.takeUnless { it.isBlank() || it.equals("False", ignoreCase = true) }
                ?.let { LocalDate.parse(it) }
            , // can be 'False'
            repairCost = fields["repair_cost"]
                .let { raw ->
                    if (raw.equals("False", ignoreCase = true)) {
                        java.math.BigDecimal.ZERO
                    } else {
                        raw!!.replace(".", "")
                            .replace(",", "")
                            .toBigDecimal() / 100.toBigDecimal()
                    }
                },
            policeReportNumber = fields["police_report_number"]
                ?.takeUnless { it.isBlank() || it.equals("False", ignoreCase = true) }
                ?: "",
            description = fields["description"].orEmpty(),
        )
    }

    private fun parseXmlTuple(raw: String?): Pair<Int?, String?> {
        if (raw.isNullOrBlank() || raw == "False") return Pair(null, null)
        val regex = """\[(\d+),\s*'(.*)'\]""".toRegex()
        val match = regex.find(raw) ?: return Pair(null, null)

        val id = match.groupValues[1].toIntOrNull()
        val name = match.groupValues[2]
        return Pair(id, name)
    }

}
