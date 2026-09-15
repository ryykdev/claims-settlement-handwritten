package dev.ryyk.claims.ingestion

import dev.ryyk.claims.contract.ContractEntity
import dev.ryyk.claims.contract.ContractStatus
import org.springframework.stereotype.Service
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ContractXmlParser() {

    fun parse(inputStream: InputStream): List<ContractEntity> {

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(inputStream)
        doc.documentElement.normalize()

        // get the export timestamp
        val rootElement = doc.documentElement // <export> element

        val model = rootElement.getAttribute("model")
        assert(model == "lr.contract.leasing") // we need to know if it changes
        val source = rootElement.getAttribute("source")
        assert(source == "erp") // we need to know if it changes
        val generatedAt = rootElement.getAttribute("generated_at")

        val recordNodes = doc.getElementsByTagName("record")
        val contracts = mutableListOf<ContractEntity>()

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
            contracts.add(mapToContract(fieldMap))
        }

        return contracts
    }

    private fun mapToContract(fields: Map<String, String>): ContractEntity {
        // Parse tuple format: "[2841, 'Anna Müller']"
        val (partnerId, partnerName) = parseErpTuple(fields["partner_id"])
        val (companyId, companyName) = parseErpTuple(fields["customer_company_id"])
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        // using !! because if this fails we need fix it fast
        return ContractEntity(
            updated = LocalDateTime.parse(fields["updated"]!!, dateTimeFormatter),
            name = fields["name"].orEmpty(),
            saleOrderNumber = fields["sale_order_number"].orEmpty(),
            partnerId = partnerId!!,
            partnerName = partnerName!!,
            customerCompanyId = companyId!!,
            customerCompanyName = companyName!!,
            frameNumber = fields["frame_number"].orEmpty(),
            brand = fields["brand"].orEmpty(),
            model = fields["model"].orEmpty(),

            // re-assigning fields
            netValue = fields["category"]!!.toBigDecimal(),
            startLeasing = LocalDate.parse(fields["net_value"]),
            endLeasing = fields["start_leasing"]?.takeUnless { it.isBlank() || it.equals("False", ignoreCase = true) }
                ?.let { LocalDate.parse(it) }
            , // can be 'False'
            term = fields["end_leasing"]!!.toInt(),
            insuranceRate = fields["term"]!!.toBigDecimal(),
            status = ContractStatus.fromRaw(fields["insurance_rate"]),
        )
    }

    private fun parseErpTuple(raw: String?): Pair<Int?, String?> {
        if (raw.isNullOrBlank() || raw == "False") return Pair(null, null)
        val regex = """\[(\d+),\s*'(.*)'\]""".toRegex()
        val match = regex.find(raw) ?: return Pair(null, null)

        val id = match.groupValues[1].toIntOrNull()
        val name = match.groupValues[2]
        return Pair(id, name)
    }

}
