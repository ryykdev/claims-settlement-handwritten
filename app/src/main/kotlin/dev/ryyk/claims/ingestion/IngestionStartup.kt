package dev.ryyk.claims.ingestion

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class IngestionStartup(
    private val contractImportService: ContractImportService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    @EventListener(ApplicationReadyEvent::class)
    fun runImportOnStartup() {
        logger.info("PostConstruct fired — starting contract import")
        contractImportService.importClassPathFile()
            .doOnSuccess { logger.info("Contract import finished") }
            .doOnError { logger.error("Contract import failed", it)}
            .subscribe(
                { logger.info("Contracts imported successfully") },
                { error -> logger.error("Import failed in subscribe", error) })
    }

}