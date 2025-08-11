package com.example.models.dto.responses

import com.example.models.domain.PurchaseStatus
import java.time.Instant

data class UpdateStatusResponse(
    val purchaseId: String,
    val newPurchaseStatus: PurchaseStatus,
    val lastUpdated: Instant,
)
