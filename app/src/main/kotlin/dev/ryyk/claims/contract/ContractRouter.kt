package dev.ryyk.claims.contract

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration(proxyBeanMethods = false)
class ContractRouter {

    @Bean
    fun contractRoutes(handler: ContractHandler): RouterFunction<ServerResponse> = router {
        GET("/contracts", handler::getContracts)
        GET("/contracts/{name}", handler::getContractWithClaims)
    }

}