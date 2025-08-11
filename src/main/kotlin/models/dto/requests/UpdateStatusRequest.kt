package com.example.models.dto.requests

import com.example.models.domain.PurchaseStatus

data class UpdateStatusRequest(
    val purchaseId: String,
    val newPurchaseStatus: PurchaseStatus,
)
