package dev.ryyk.claims.ingestion

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.io.support.ResourcePatternResolver
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class IngestionStartup(
    private val contractImportService: ContractImportService,
    private val claimImportService: ClaimImportService,
    private val resourcePatternResolver: ResourcePatternResolver
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    @EventListener(ApplicationReadyEvent::class)
    fun runImportOnStartup() {
        logger.info("ImportOnStartUp fired — starting imports")
        contractImportService.importClassPathFile()
            .doOnSuccess { logger.info("Contract import finished") }
            .doOnError { logger.error("Contract import failed", it)}
            .then(importAllClaimBatches())
            .subscribe(
                { logger.info("Contracts imported successfully") },
                { error -> logger.error("Contract Import failed in subscribe", error) })
    }
        // claims import

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
            .map {"data/$it"}
    }

}