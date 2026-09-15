package dev.ryyk.claims.decision

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface DecisionRepository: ReactiveCrudRepository<DecisionEntity, Long> {
}