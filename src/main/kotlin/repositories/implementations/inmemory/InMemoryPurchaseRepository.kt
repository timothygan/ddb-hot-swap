package com.example.repositories.implementations.inmemory

import com.example.models.dto.requests.CreatePurchaseRequest
import com.example.models.dto.requests.CreateUserRequest
import com.example.models.dto.requests.GetAllPurchasesForUserRequest
import com.example.models.dto.requests.GetPurchaseRequest
import com.example.models.dto.requests.GetUserRequest
import com.example.models.dto.requests.UpdateStatusRequest
import com.example.models.dto.responses.CreatePurchaseResponse
import com.example.models.dto.responses.CreateUserResponse
import com.example.models.dto.responses.GetAllPurchasesForUserResponse
import com.example.models.dto.responses.GetUserResponse
import com.example.models.dto.responses.UpdateStatusResponse
import com.example.repositories.interfaces.PurchaseRepository
import com.example.repositories.interfaces.UserRepository

class InMemoryPurchaseRepository: PurchaseRepository {
    override fun createPurchase(createPurchaseRequest: CreatePurchaseRequest): CreatePurchaseResponse {
        TODO("Not yet implemented")
    }

    override fun updatePurchaseStatus(updateStatusRequest: UpdateStatusRequest): UpdateStatusResponse {
        TODO("Not yet implemented")
    }

    override fun getPurchase(getPurchaseRequest: GetPurchaseRequest): GetPurchaseRequest {
        TODO("Not yet implemented")
    }

    override fun getAllPurchasesForUser(getAllPurchasesForUserRequest: GetAllPurchasesForUserRequest): GetAllPurchasesForUserResponse {
        TODO("Not yet implemented")
    }
}