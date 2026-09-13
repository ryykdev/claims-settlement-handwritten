package dev.ryyk.claims.ingestion

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration(proxyBeanMethods = false)
class IngestionRouter {
    @Bean // Bean will be named after its method, route already exists therefore ingestionRoute
    fun ingestionRoute(handler: IngestionHandler): RouterFunction<ServerResponse> = router {
        POST ( "/ingest", handler::ingest )
    }

}