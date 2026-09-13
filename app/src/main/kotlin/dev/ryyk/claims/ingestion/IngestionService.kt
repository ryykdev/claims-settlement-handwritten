package dev.ryyk.claims.ingestion

import org.slf4j.LoggerFactory
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class IngestionService(
    private val contractImportService: ContractImportService,
    private val claimImportService: ClaimImportService,
    private val resourcePatternResolver: ResourcePatternResolver
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun runImport(): Mono<Void> {
        return contractImportService.importClassPathFile()
            .doOnSuccess { logger.info("Contract import finished") }
            .doOnError { logger.error("Contract import failed", it) }
            .then(importAllClaimBatches())
    }

    private fun importAllClaimBatches(): Mono<Void> {
        val claimBatches = discoverClaimBatchFiles()
        logger.info("Found claim batches: $claimBatches")
        return Flux.fromIterable(claimBatches)
            .concatMap { fileName ->
                logger.info("Importing $fileName")
                claimImportService.importClassPathFile(fileName)
            }
            .then()
    }

    private fun discoverClaimBatchFiles(): List<String> {
        return resourcePatternResolver
            .getResources("classpath:data/claims_batch_*.xml")
            .mapNotNull { it.filename }
            .sorted()
            .map { "data/$it" }

    }

}