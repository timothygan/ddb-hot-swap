package com.example.models.dto.requests

data class GetPurchaseRequest(
    val userId: Int,
    val purchaseId: Int,
)
