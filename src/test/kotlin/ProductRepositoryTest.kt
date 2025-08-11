import com.example.models.domain.Product
import com.example.models.dto.requests.CreateProductRequest
import com.example.models.dto.requests.GetProductRequest
import com.example.repositories.implementations.inmemory.InMemoryProductRepository
import com.example.repositories.interfaces.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty

class ProductRepositoryTest: FunSpec ({
    lateinit var repository: ProductRepository

    beforeEach {
        repository = InMemoryProductRepository()
    }

    test("should create a new product successfully") {
        // Act
        val createProductRequest = CreateProductRequest("my new product", 10.0)
        val createProductResponse = repository.createProduct(createProductRequest)

        // Assert
        createProductResponse.id.shouldNotBeEmpty()
    }

    test("should get a product successfully") {
        // Act
        val createProductRequest = CreateProductRequest("my new product", 10.0)
        val id = repository.createProduct(createProductRequest).id
        val getProductRequest = GetProductRequest(id)
        val product = repository.getProduct(getProductRequest)

        //Assert
        product.id.shouldNotBeEmpty()
        product.name shouldBe "my new product"
        product.price shouldBe 10.0
    }

    test("should get products successfully") {
        // Act
        val createProductRequest = CreateProductRequest("my new product 1", 10.0)
        val createProductRequest2 = CreateProductRequest("my new product 2", 15.0)
        val id1 = repository.createProduct(createProductRequest).id
        val id2 = repository.createProduct(createProductRequest2).id
        val idToProduct = mapOf(
            id1 to Product(id1, "my new product 1", 10.0),
            id2 to Product(id2,"my new product 2", 15.0),
        )

        val getAllProductsResponse = repository.getAllProducts()

        //Assert
        getAllProductsResponse.shouldHaveSize(2)
        getAllProductsResponse.forEach { response ->
            response.id.shouldNotBeEmpty()
            response.name shouldBe idToProduct[response.id]?.name
            response.price shouldBe idToProduct[response.id]?.price
        }
    }

    test("should throw if product not found") {
        //Act
        val getProductRequest = GetProductRequest("nonexistent-id")

        //Assert
        shouldThrow<RuntimeException> {
            repository.getProduct(getProductRequest)
        }
    }

    test("should return empty list if no products") {
        val getAllProductsResponse = repository.getAllProducts()

        getAllProductsResponse.shouldHaveSize(0)
    }
})