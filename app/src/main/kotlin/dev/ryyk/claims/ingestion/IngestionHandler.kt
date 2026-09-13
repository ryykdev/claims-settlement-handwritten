package dev.ryyk.claims.ingestion

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class IngestionHandler(
    private val ingestionService: IngestionService
) {

    fun ingest(request: ServerRequest): Mono<ServerResponse> {
        return ingestionService.runImport()
            .then(ServerResponse.accepted().build()) // async processing started
    }
}