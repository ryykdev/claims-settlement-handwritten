package dev.ryyk.claims.ingestion

import dev.ryyk.claims.claim.ClaimEntity
import dev.ryyk.claims.contract.ContractRepository
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.contracts.contract

@Service
class ContractImportService(
    private val contractRepository: ContractRepository
) {

    private val parser = ContractXmlParser()

    @Transactional
    fun importClassPathFile(fileName: String = "data/contracts_export.xml"): Mono<Void> {
        val contracts = ClassPathResource(fileName).inputStream.use { parser.parse(it) }
        return Flux.fromIterable(contracts)
            .concatMap { contract ->
               contractRepository.existsById(contract.name)
                   .map { exists ->
                       if (exists) contract.also {it.markExisting()} else contract
                   }
             }
            .collectList()
            .flatMap { contractRepository.saveAll(it).then() }
    }

}