package com.example.models

data class Purchase(
    val id: Int,
    val userId: Int,
    val productId: Int,
    val quantity: Int,
    val totalPrice: Double,
    val purchaseDate: String,
    val status: PurchaseStatus,
)
