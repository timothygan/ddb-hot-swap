package com.example.models.domain

import java.time.Instant

data class Purchase(
    val id: Int,
    val userId: Int,
    val productId: Int,
    val quantity: Int,
    val totalPrice: Double,
    val purchaseDate: String,
    val lastUpdated: Instant,
    val status: PurchaseStatus,
)
