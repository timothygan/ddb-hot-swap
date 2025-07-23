package com.example.models.dto.requests

data class CreatePurchaseRequest(
    val userId: String,
    val productId: Int,
    val quantity: Int,
)
