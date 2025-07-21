package com.example.models.dto.responses

data class CreatePurchaseResponse(
    val userId: Int,
    val totalPrice: Double,
    val purchaseDate: String,
)
