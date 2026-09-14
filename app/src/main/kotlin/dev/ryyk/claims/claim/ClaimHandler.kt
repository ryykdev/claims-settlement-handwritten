package dev.ryyk.claims.claim

import dev.ryyk.claims.contract.ClaimRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class ClaimHandler(
    private val claimRepository: ClaimRepository
) {

    fun getClaims(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
            .body(claimRepository.findAll(), ClaimEntity::class.java)
    }

}