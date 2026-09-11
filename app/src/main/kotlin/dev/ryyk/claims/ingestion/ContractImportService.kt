package dev.ryyk.claims.ingestion

import dev.ryyk.claims.contract.ContractRepository
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class ContractImportService(
    private val contractRepository: ContractRepository
) {

    private val parser = ContractXmlParser()

    @Transactional
    fun importClassPathFile(fileName: String = "data/contracts_export.xml"): Mono<Void> {
        val contracts = ClassPathResource(fileName).inputStream.use { parser.parse(it) }
        return contractRepository.saveAll(contracts).then()
    }

}