package dev.ryyk.claims.decision

import dev.ryyk.claims.claim.ClaimEntity
import dev.ryyk.claims.contract.ClaimRepository
import dev.ryyk.claims.contract.ContractRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import kotlin.collections.emptyList

@Component
class DecisionHandler(
    private val decisionRepository: DecisionRepository,
    private val claimRepository: ClaimRepository,
    private val contractRepository: ContractRepository,
    private val rules: DecisionRules,
) {

    fun decideClaim(serverRequest: ServerRequest): Mono<ServerResponse> {
        val externalId = serverRequest.pathVariable("id").toLongOrNull()
            ?.takeIf { it > 0 }
            ?: return ServerResponse.badRequest().bodyValue("ID must be an positive non-zero Integer")

        return claimRepository.findById(externalId)
            .flatMap { claimEntity -> LoadContextAndDecideClaim(claimEntity) }
            .flatMap { decisionEntity ->
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(decisionEntity)
            }
            .switchIfEmpty(ServerResponse.notFound().build())
    }

    fun LoadContextAndDecideClaim(claim: ClaimEntity): Mono<DecisionEntity> {
        // idempotency
        return decisionRepository.findById(claim.externalId)
            .switchIfEmpty(decideFresh(claim))
    }

    // return 409 if decision is already FINAL

    fun decideFresh(claim: ClaimEntity): Mono<DecisionEntity> {
        val saleOrderNumber = claim.saleOrderNumber
            ?: return Mono.just(rules.processClaim(claim, null, emptyList()))
                .flatMap { decisionRepository.save(it) }
        return contractRepository.findBySaleOrderNumber(saleOrderNumber)
            .next()
            .flatMap { contractEntity ->
                claimRepository.findBySaleOrderNumber(saleOrderNumber)
                    .filter { it.saleOrderNumber != claim.saleOrderNumber }
                    .collectList()
                    .map { associatedClaims ->
                        rules.processClaim(claim, contractEntity, associatedClaims)
                    }
                    .flatMap { decisionEntity -> decisionRepository.save(decisionEntity) }
            }
    }




    }