package dev.ryyk.claims.ingestion

import dev.ryyk.claims.contract.ClaimRepository
import dev.ryyk.claims.contract.ContractRepository
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class ClaimImportService(
    private val claimRepository: ClaimRepository,
) {

    private val parser = ClaimXmlParser()

    @Transactional
    fun importClassPathFile(fileName: String): Mono<Void> {
        val claims = ClassPathResource(fileName).inputStream.use { parser.parse(it) }
        return Flux.fromIterable(claims)
            .concatMap { claim ->
                claimRepository.existsById(claim.externalId)
                    .doOnNext { exists ->
                        if (exists) claim.markExisting() // mark the entity existing (isNew = false)
                    }
                    .thenReturn(claim)
            }
            .collectList()
            .flatMap { claimRepository.saveAll(it).then() } // then() lets the Flux complete and return a Mono
    }

}