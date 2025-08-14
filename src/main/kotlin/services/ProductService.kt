package com.example.services

import com.example.models.dto.requests.CreateProductRequest
import com.example.models.dto.responses.CreateProductResponse
import com.example.models.dto.responses.GetProductResponse
import com.example.repositories.interfaces.ProductRepository

class ProductService(
    private val productRepository: ProductRepository
) {
    fun createProduct(name: String, price: Double): ServiceResult<CreateProductResponse> =
        try {
            validateCreateProductRequest(name, price)
                ?.let {
                    ServiceResult.Error(
                        it
                    )
                } ?: productRepository
                .createProduct(
                    CreateProductRequest(
                        name,
                        price
                    )
                ).let {
                        ServiceResult.Success(it)
                    }
        } catch (e: Exception) {
            ServiceResult.Error(
                ServiceError.UnknownError(
                    e.message ?: e.javaClass.simpleName,
                    e
                )
            )
        }
    
    fun getProduct(productId: String): ServiceResult<GetProductResponse> {
        TODO("Implement product retrieval - validate productId not empty, handle repository exceptions")
    }
    
    fun getAllProducts(): ServiceResult<List<GetProductResponse>> {
        TODO("Implement get all products - handle repository exceptions")
    }
    
    private fun validateCreateProductRequest(name: String, price: Double): ServiceError? =
        when {
            name.isBlank() -> {
                ServiceError.ValidationError(
                    "Product name cannot be empty",
                    Exception()
                )
            }

            name.isEmpty() -> {
                ServiceError.ValidationError(
                    "Product name cannot be empty",
                    Exception()
                )
            }

            name.length > 100 -> {
                ServiceError.ValidationError(
                    "Product name cannot exceed 100 characters",
                    Exception()
                )
            }

            price <= 0.0 -> {
                ServiceError.ValidationError(
                    "Product price must be greater than zero",
                    Exception()
                )
            }

            price > 10000 -> {
                ServiceError.ValidationError(
                    "Price must be less than 10000",
                    Exception()
                )
            }

            else -> null
        }

    
    private fun validateProductId(productId: String): ServiceError? {
        TODO("Implement validation: empty productId")
    }
}