package com.example.models.dto.responses

data class CreatePurchaseResponse(
    val userId: String,
    val totalPrice: Double,
    val purchaseDate: String,
)
