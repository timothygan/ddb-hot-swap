import com.example.models.dto.requests.CreateProductRequest
import com.example.models.dto.requests.GetProductRequest
import com.example.models.dto.responses.CreateProductResponse
import com.example.models.dto.responses.GetProductResponse
import com.example.repositories.interfaces.ProductRepository
import com.example.services.ServiceError
import com.example.services.ServiceResult
import com.example.services.ProductService
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*

class ProductServiceTest : FunSpec({
    
    lateinit var mockProductRepository: ProductRepository
    lateinit var productService: ProductService
    
    beforeEach {
        mockProductRepository = mockk()
        productService = ProductService(mockProductRepository)
    }
    
    afterEach {
        clearAllMocks()
    }
    
    test("should create product successfully when valid request") {
        // Arrange
        val name = "Valid Product"
        val price = 29.99
        val request = CreateProductRequest(name, price)
        val expectedResponse = CreateProductResponse("product123")
        every { mockProductRepository.createProduct(request) } returns expectedResponse
        
        // Act
        val result = productService.createProduct(name, price)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<CreateProductResponse>>()
        result.data shouldBe expectedResponse
        verify { mockProductRepository.createProduct(request) }
    }
    
    test("should return validation error when product name is empty") {
        // Arrange
        val name = ""
        val price = 29.99

        // Act
        val result = productService.createProduct(name, price)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Product name cannot be empty"
        verify(exactly = 0) { mockProductRepository.createProduct(any()) }
    }
    
    test("should return validation error when product name is blank") {
        // Arrange
        val name = "   "
        val price = 29.99

        // Act
        val result = productService.createProduct(name, price)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Product name cannot be empty"
        verify(exactly = 0) { mockProductRepository.createProduct(any()) }
    }
    
    test("should return validation error when price is zero") {
        // Arrange
        val name = "Valid Product"
        val price = 0.0

        // Act
        val result = productService.createProduct(name, price)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Product price must be greater than zero"
        verify(exactly = 0) { mockProductRepository.createProduct(any()) }
    }
    
    test("should return validation error when price is negative") {
        // Arrange
        val name = "Valid Product"
        val price = -10.00

        // Act
        val result = productService.createProduct(name, price)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Product price must be greater than zero"
        verify(exactly = 0) { mockProductRepository.createProduct(any()) }
    }
    
    test("should return validation error when product name is too long") {
        // Arrange
        val longName = "a".repeat(101) // Assuming 100 char limit
        val price = 29.99

        // Act
        val result = productService.createProduct(longName, price)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Product name cannot exceed 100 characters"
        verify(exactly = 0) { mockProductRepository.createProduct(any()) }
    }
    
    test("should handle repository exceptions gracefully during creation") {
        // Arrange
        val name = "Valid Product"
        val price = 29.99
        val request = CreateProductRequest(name, price)
        every { mockProductRepository.createProduct(request) } throws RuntimeException("Database error")
        
        // Act
        val result = productService.createProduct(name, price)

        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.UnknownError>()
        result.error.message shouldBe "Database error"
        verify { mockProductRepository.createProduct(request) }
    }
    
    test("should retrieve product successfully by id") {
        // Arrange
        val productId = "product123"
        val expectedResponse = GetProductResponse("product123", "Test Product", 29.99)
        every { mockProductRepository.getProduct(GetProductRequest(productId)) } returns expectedResponse
        
        // Act
        val result = productService.getProduct(productId)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<GetProductResponse>>()
        result.data shouldBe expectedResponse
        verify { mockProductRepository.getProduct(GetProductRequest(productId)) }
    }
    
    test("should return validation error when product id is empty") {
        // Arrange
        val productId = ""
        
        // Act
        val result = productService.getProduct(productId)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Product ID cannot be empty"
        verify(exactly = 0) { mockProductRepository.getProduct(any()) }
    }
    
    test("should return not found error when product does not exist") {
        // Arrange
        val productId = "nonexistent"
        every { mockProductRepository.getProduct(GetProductRequest(productId)) } throws RuntimeException("Product not found")
        
        // Act
        val result = productService.getProduct(productId)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.NotFound>()
        result.error.message shouldBe "Product with id nonexistent not found"
        verify { mockProductRepository.getProduct(GetProductRequest(productId)) }
    }
    
    test("should retrieve all products successfully") {
        // Arrange
        val expectedProducts = listOf(
            GetProductResponse("product1", "Product 1", 19.99),
            GetProductResponse("product2", "Product 2", 39.99)
        )
        every { mockProductRepository.getAllProducts() } returns expectedProducts
        
        // Act
        val result = productService.getAllProducts()
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<List<GetProductResponse>>>()
        result.data shouldBe expectedProducts
        verify { mockProductRepository.getAllProducts() }
    }
    
    test("should return empty list when no products exist") {
        // Arrange
        every { mockProductRepository.getAllProducts() } returns emptyList()
        
        // Act
        val result = productService.getAllProducts()
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<List<GetProductResponse>>>()
        result.data shouldBe emptyList()
        verify { mockProductRepository.getAllProducts() }
    }
    
    test("should return business rule violation when price exceeds maximum") {
        // Arrange
        val name = "Expensive Product"
        val price = 10000.01

        // Act
        val result = productService.createProduct(name, price)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.BusinessRuleViolation>()
        result.error.message shouldBe "Price must be less than 10000"
        verify(exactly = 0) { mockProductRepository.createProduct(any()) }
    }
})