package com.example.repositories.implementations.inmemory

import com.example.models.domain.Purchase
import com.example.models.domain.PurchaseStatus
import com.example.models.dto.requests.CreatePurchaseRequest
import com.example.models.dto.requests.GetAllPurchasesForUserRequest
import com.example.models.dto.requests.GetPurchaseRequest
import com.example.models.dto.requests.UpdateStatusRequest
import com.example.models.dto.responses.CreatePurchaseResponse
import com.example.models.dto.responses.GetAllPurchasesForUserResponse
import com.example.models.dto.responses.GetPurchaseResponse
import com.example.models.dto.responses.UpdateStatusResponse
import com.example.repositories.interfaces.PurchaseRepository
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class InMemoryPurchaseRepository : PurchaseRepository {
    private val store = mutableMapOf<String, Purchase>()

    override fun createPurchase(createPurchaseRequest: CreatePurchaseRequest): CreatePurchaseResponse =
        UUID.randomUUID().toString().let { purchaseId ->
            val purchase = Purchase(
                id = purchaseId,
                userId = createPurchaseRequest.userId,
                productId = createPurchaseRequest.productId,
                quantity = createPurchaseRequest.quantity,
                totalPrice = createPurchaseRequest.totalPrice,
                purchaseDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                lastUpdated = Instant.now(),
                status = PurchaseStatus.PENDING
            )
            store[purchaseId] = purchase
            CreatePurchaseResponse(purchaseId)
        }

    override fun updatePurchaseStatus(updateStatusRequest: UpdateStatusRequest): UpdateStatusResponse =
        store[updateStatusRequest.purchaseId]?.let { existingPurchase ->
            val updatedPurchase = existingPurchase.copy(
                status = updateStatusRequest.newPurchaseStatus,
                lastUpdated = Instant.now()
            )
            store[updateStatusRequest.purchaseId] = updatedPurchase
            UpdateStatusResponse(
                purchaseId = updateStatusRequest.purchaseId,
                newPurchaseStatus = updateStatusRequest.newPurchaseStatus,
                lastUpdated = updatedPurchase.lastUpdated,
            )
        } ?: throw RuntimeException("Purchase not found")

    override fun getPurchase(getPurchaseRequest: GetPurchaseRequest): GetPurchaseResponse =
        store[getPurchaseRequest.purchaseId]?.let { purchase ->
            GetPurchaseResponse(purchase)
        } ?: throw RuntimeException("Purchase not found")

    override fun getAllPurchasesForUser(getAllPurchasesForUserRequest: GetAllPurchasesForUserRequest): GetAllPurchasesForUserResponse =
        GetAllPurchasesForUserResponse(
            purchases = store.values
                .filter { it.userId == getAllPurchasesForUserRequest.userId }
                .toList()
        )
}