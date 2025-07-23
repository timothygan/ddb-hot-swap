package com.example.repositories.implementations.inmemory

import com.example.models.dto.requests.CreateProductRequest
import com.example.models.dto.requests.GetProductRequest
import com.example.models.dto.responses.CreateProductResponse
import com.example.models.dto.responses.GetProductResponse
import com.example.repositories.interfaces.ProductRepository

class InMemoryProductRepository: ProductRepository {

    override fun createProduct(product: CreateProductRequest): CreateProductResponse {
        TODO("Not yet implemented")
    }

    override fun getProduct(productId: GetProductRequest): GetProductResponse {
        TODO("Not yet implemented")
    }

    override fun getAllProducts(): List<GetProductResponse> {
        TODO("Not yet implemented")
    }
}