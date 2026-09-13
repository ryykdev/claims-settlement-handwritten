package dev.ryyk.claims.ingestion

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class IngestionStartup(
    private val ingestionService: IngestionService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun runImportOnStartup() {
        logger.info("ImportOnStartUp fired — starting imports")
        ingestionService.runImport()
            .subscribe(
                { logger.info("Ingestion run successfull") },
                { error -> logger.error("Ingestionr run failed with:", error) })

    }
}
