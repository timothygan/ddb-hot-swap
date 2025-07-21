package com.example.models.dto.responses

import com.example.models.domain.Purchase

data class GetAllPurchasesForUserResponse(
    val purchases: List<Purchase>,
)
