package dev.ryyk.claims.ingestion

import dev.ryyk.claims.contract.ContractRepository
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class ClaimImportService(
    private val contractRepository: ContractRepository
) {

    private val parser = ContractXmlParser()

    @Transactional
    fun importClassPathFile(fileName: String = "data/claims_batch_1.xml"): Mono<Void> {
        val claims = ClassPathResource(fileName).inputStream.use { parser.parse(it) }
        return claimRespository.saveAll(claims).then()
    }

}