package dev.ryyk.claims.contract

import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class ContractHandler(
    private val contractRepository: ContractRepository,
    private val claimRepository: ClaimRepository
) {

    fun getContracts(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(contractRepository.findAll(),
            ContractEntity::class.java)
    }

    fun getContractWithClaims(request: ServerRequest): Mono<ServerResponse> {
        val name = request.pathVariable("name")
        return contractRepository.findById(name)
            .flatMap { contract ->
                claimRepository.findBySaleOrderNumber(contract.saleOrderNumber)
                    .collectList()
                    .map { claims -> ContractDetailResponse(contract, claims) }
            }
            .flatMap { detailResponse ->
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(detailResponse)
            }
            .switchIfEmpty(ServerResponse.notFound().build())
    }
}