package dev.ryyk.claims.contract

import dev.ryyk.claims.decision.DecisionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration(proxyBeanMethods = false)
class DecisionRouter {

    @Bean
    fun decisionRoutes(handler: DecisionHandler): RouterFunction<ServerResponse> = router {
        GET("/claims/{externalId}/decision", handler::decideClaim)
    }

}
