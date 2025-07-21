package com.example.models.dto.requests

data class CreatePurchaseRequest(
    val userId: Int,
    val productId: Int,
    val quantity: Int,
)
