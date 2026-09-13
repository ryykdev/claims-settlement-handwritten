package dev.ryyk.claims.ingestion

import dev.ryyk.claims.claim.ClaimEntity
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
    private val contractRepository: ContractRepository
) {

    private val parser = ClaimXmlParser()

    @Transactional
    fun importClassPathFile(fileName: String): Mono<Void> {
        val claims = ClassPathResource(fileName).inputStream.use { parser.parse(it) }
        return Flux.fromIterable(claims)
            .concatMap { claim ->
                claimRepository.existsById(claim.externalId)
                    .flatMap { exists ->
                        resolveContractLink(claim)
                            .map { resolved ->
                                if (exists) resolved.also { it.markExisting() } else resolved
                            }
                    }
            }
            .collectList()
            .flatMap { claimRepository.saveAll(it).then() } // then() lets the Flux complete and return a Mono
    }

    // this allows orphan claim.saleOrderNumber = null, for claims without contracts
    private fun resolveContractLink(claim: ClaimEntity): Mono<ClaimEntity> {
        val saleOrderNumber = claim.saleOrderNumber;
        if (saleOrderNumber.isNullOrBlank()) {
            return Mono.just(claim)
        }
        return contractRepository.findBySaleOrderNumber(saleOrderNumber)
            .hasElements()
            .map { contractExists ->
                if (contractExists) claim else claim.copy(saleOrderNumber = null)
            }
    }

}