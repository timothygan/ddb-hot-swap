package com.example.services

import com.example.models.dto.requests.CreateProductRequest
import com.example.models.dto.responses.CreateProductResponse
import com.example.models.dto.responses.GetProductResponse
import com.example.repositories.interfaces.ProductRepository

class ProductService(
    private val productRepository: ProductRepository
) {
    fun createProduct(request: CreateProductRequest): ServiceResult<CreateProductResponse> {
        TODO("Implement product creation with validation - validate name not empty, price > 0, price <= max, name length")
    }
    
    fun getProduct(productId: String): ServiceResult<GetProductResponse> {
        TODO("Implement product retrieval - validate productId not empty, handle repository exceptions")
    }
    
    fun getAllProducts(): ServiceResult<List<GetProductResponse>> {
        TODO("Implement get all products - handle repository exceptions")
    }
    
    private fun validateCreateProductRequest(request: CreateProductRequest): ServiceError? {
        TODO("Implement validation: empty/blank name, price <= 0, price > 10000, name length > 100")
    }
    
    private fun validateProductId(productId: String): ServiceError? {
        TODO("Implement validation: empty productId")
    }
}