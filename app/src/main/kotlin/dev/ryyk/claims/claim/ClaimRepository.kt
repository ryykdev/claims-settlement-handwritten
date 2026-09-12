package dev.ryyk.claims.contract


import dev.ryyk.claims.claim.ClaimEntity
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface ClaimRepository : ReactiveCrudRepository<ClaimEntity, Long> {
    fun findByExternalId(externalId: Long): Flux<ClaimEntity>
}
