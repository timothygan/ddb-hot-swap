package com.example.models.dto.requests

import com.example.models.domain.PurchaseStatus

data class UpdateStatusRequest(
    val userId: Int,
    val productId: Int,
    val newPurchaseStatus: PurchaseStatus,
)
