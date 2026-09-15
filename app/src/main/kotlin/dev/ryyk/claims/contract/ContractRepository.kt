package dev.ryyk.claims.contract


import dev.ryyk.claims.claim.ClaimEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface ContractRepository : ReactiveCrudRepository<ContractEntity, String> {
    fun findBySaleOrderNumber(saleOrderNumber: String): Flux<ContractEntity>
}
