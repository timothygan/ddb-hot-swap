package com.example.models.dto.responses

import com.example.models.domain.PurchaseStatus

data class UpdateStatusResponse(
    val lastUpdated: PurchaseStatus,
)
