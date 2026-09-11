package dev.ryyk.claims.contract


import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ContractRepository : ReactiveCrudRepository<ContractEntity, String> {
    fun findBySaleOrderNumber(saleOrderNumber: String): Flux<ContractEntity>
}
