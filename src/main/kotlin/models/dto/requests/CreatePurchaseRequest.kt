package com.example.models.dto.requests

data class CreatePurchaseRequest(
    val userId: String,
    val productId: String,
    val quantity: Int,
    val totalPrice: Double,
)
