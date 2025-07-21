package com.example.repositories.interfaces

import com.example.models.domain.Product
import com.example.models.dto.requests.CreateProductRequest
import com.example.models.dto.requests.GetProductRequest
import com.example.models.dto.responses.CreateProductResponse
import com.example.models.dto.responses.GetProductResponse

interface ProductRepository {
    fun createProduct(product: CreateProductRequest): CreateProductResponse
    fun getProduct(productId: GetProductRequest): GetProductResponse
    fun getAllProducts(): List<GetProductResponse>
}