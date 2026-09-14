package dev.ryyk.claims.claim

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration(proxyBeanMethods = false)
class ClaimRouter {

    @Bean
    fun claimRoutes(handler: ClaimHandler): RouterFunction<ServerResponse> = router {
        GET("/claims", handler::getClaims)
    }

}