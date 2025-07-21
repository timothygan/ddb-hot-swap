package com.example.repositories.interfaces

import com.example.models.dto.requests.CreatePurchaseRequest
import com.example.models.dto.requests.GetAllPurchasesForUserRequest
import com.example.models.dto.requests.GetPurchaseRequest
import com.example.models.dto.requests.UpdateStatusRequest
import com.example.models.dto.responses.CreatePurchaseResponse
import com.example.models.dto.responses.GetAllPurchasesForUserResponse
import com.example.models.dto.responses.UpdateStatusResponse

interface PurchaseRepository {
    fun createPurchase(createPurchaseRequest: CreatePurchaseRequest): CreatePurchaseResponse
    fun updatePurchaseStatus(updateStatusRequest: UpdateStatusRequest): UpdateStatusResponse
    fun getPurchase(getPurchaseRequest: GetPurchaseRequest): GetPurchaseRequest
    fun getAllPurchasesForUser(getAllPurchasesForUserRequest: GetAllPurchasesForUserRequest): GetAllPurchasesForUserResponse
}