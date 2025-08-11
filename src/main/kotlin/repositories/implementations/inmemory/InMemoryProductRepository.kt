package com.example.repositories.implementations.inmemory

import com.example.models.domain.Product
import com.example.models.dto.requests.CreateProductRequest
import com.example.models.dto.requests.GetProductRequest
import com.example.models.dto.responses.CreateProductResponse
import com.example.models.dto.responses.GetProductResponse
import com.example.repositories.interfaces.ProductRepository
import java.util.UUID

class InMemoryProductRepository: ProductRepository {
    private val store = mutableMapOf<String, Product>()

    override fun createProduct(product: CreateProductRequest): CreateProductResponse =
        UUID
            .randomUUID()
            .toString()
            .let { productId ->
                store.putIfAbsent(
                    productId,
                    Product(
                        productId,
                        product.name,
                        product.price
                    )
                )
                CreateProductResponse(productId)
        }

    override fun getProduct(productId: GetProductRequest): GetProductResponse =
        store.get(
            productId.productId
        )?.let {
            GetProductResponse(
                it.id,
                it.name,
                it.price
            )
        } ?: throw RuntimeException("Product not found")

    override fun getAllProducts(): List<GetProductResponse> =
        store.values.map {
            GetProductResponse(
                it.id,
                it.name,
                it.price
            )
        }
}