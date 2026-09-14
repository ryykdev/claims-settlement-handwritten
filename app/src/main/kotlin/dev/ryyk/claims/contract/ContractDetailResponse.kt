package dev.ryyk.claims.contract

import dev.ryyk.claims.claim.ClaimEntity

data class ContractDetailResponse(
    val contract: ContractEntity,
    val claims: List<ClaimEntity>
)
