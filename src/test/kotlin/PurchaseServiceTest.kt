import com.example.models.domain.PurchaseStatus
import com.example.models.dto.requests.*
import com.example.models.dto.responses.*
import com.example.repositories.interfaces.PurchaseRepository
import com.example.repositories.interfaces.UserRepository
import com.example.repositories.interfaces.ProductRepository
import com.example.services.ServiceError
import com.example.services.ServiceResult
import com.example.services.PurchaseService
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import java.time.Instant

class PurchaseServiceTest : FunSpec({
    
    lateinit var mockPurchaseRepository: PurchaseRepository
    lateinit var mockUserRepository: UserRepository
    lateinit var mockProductRepository: ProductRepository
    lateinit var purchaseService: PurchaseService
    
    beforeEach {
        mockPurchaseRepository = mockk()
        mockUserRepository = mockk()
        mockProductRepository = mockk()
        purchaseService = PurchaseService(mockPurchaseRepository, mockUserRepository, mockProductRepository)
    }
    
    afterEach {
        clearAllMocks()
    }
    
    test("should create purchase successfully when all validations pass") {
        // Arrange
        val request = CreatePurchaseRequest("user123", "product456", 2, 59.98)
        val userResponse = GetUserResponse("user123", "testuser")
        val productResponse = GetProductResponse("product456", "Test Product", 29.99)
        val expectedResponse = CreatePurchaseResponse("purchase789")
        
        every { mockUserRepository.getUser(GetUserRequest("user123")) } returns userResponse
        every { mockProductRepository.getProduct(GetProductRequest("product456")) } returns productResponse
        every { mockPurchaseRepository.createPurchase(request) } returns expectedResponse
        
        // Act
        val result = purchaseService.createPurchase(request)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<CreatePurchaseResponse>>()
        result.data shouldBe expectedResponse
        verify { mockUserRepository.getUser(GetUserRequest("user123")) }
        verify { mockProductRepository.getProduct(GetProductRequest("product456")) }
        verify { mockPurchaseRepository.createPurchase(request) }
    }
    
    test("should return validation error when user id is empty") {
        // Arrange
        val request = CreatePurchaseRequest("", "product456", 2, 59.98)
        
        // Act
        val result = purchaseService.createPurchase(request)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "User ID cannot be empty"
        verify(exactly = 0) { mockUserRepository.getUser(any()) }
    }
    
    test("should return validation error when product id is empty") {
        // Arrange
        val request = CreatePurchaseRequest("user123", "", 2, 59.98)
        
        // Act
        val result = purchaseService.createPurchase(request)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Product ID cannot be empty"
        verify(exactly = 0) { mockUserRepository.getUser(any()) }
    }
    
    test("should return validation error when quantity is zero") {
        // Arrange
        val request = CreatePurchaseRequest("user123", "product456", 0, 0.0)
        
        // Act
        val result = purchaseService.createPurchase(request)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Quantity must be greater than zero"
        verify(exactly = 0) { mockUserRepository.getUser(any()) }
    }
    
    test("should return validation error when total price is negative") {
        // Arrange
        val request = CreatePurchaseRequest("user123", "product456", 2, -10.0)
        
        // Act
        val result = purchaseService.createPurchase(request)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Total price must be greater than zero"
        verify(exactly = 0) { mockUserRepository.getUser(any()) }
    }
    
    test("should return business rule violation when calculated total does not match provided total") {
        // Arrange
        val request = CreatePurchaseRequest("user123", "product456", 2, 100.0) // Wrong total
        val userResponse = GetUserResponse("user123", "testuser")
        val productResponse = GetProductResponse("product456", "Test Product", 29.99)
        
        every { mockUserRepository.getUser(GetUserRequest("user123")) } returns userResponse
        every { mockProductRepository.getProduct(GetProductRequest("product456")) } returns productResponse
        
        // Act
        val result = purchaseService.createPurchase(request)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.BusinessRuleViolation>()
        result.error.message shouldBe "Provided total (100.0) does not match calculated total (59.98)"
        verify { mockUserRepository.getUser(GetUserRequest("user123")) }
        verify { mockProductRepository.getProduct(GetProductRequest("product456")) }
        verify(exactly = 0) { mockPurchaseRepository.createPurchase(any()) }
    }
    
    test("should return not found error when user does not exist") {
        // Arrange
        val request = CreatePurchaseRequest("nonexistent", "product456", 2, 59.98)
        every { mockUserRepository.getUser(GetUserRequest("nonexistent")) } throws RuntimeException("User not found")
        
        // Act
        val result = purchaseService.createPurchase(request)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.NotFound>()
        result.error.message shouldBe "User with id nonexistent not found"
        verify { mockUserRepository.getUser(GetUserRequest("nonexistent")) }
        verify(exactly = 0) { mockProductRepository.getProduct(any()) }
    }
    
    test("should return not found error when product does not exist") {
        // Arrange
        val request = CreatePurchaseRequest("user123", "nonexistent", 2, 59.98)
        val userResponse = GetUserResponse("user123", "testuser")
        
        every { mockUserRepository.getUser(GetUserRequest("user123")) } returns userResponse
        every { mockProductRepository.getProduct(GetProductRequest("nonexistent")) } throws RuntimeException("Product not found")
        
        // Act
        val result = purchaseService.createPurchase(request)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.NotFound>()
        result.error.message shouldBe "Product with id nonexistent not found"
        verify { mockUserRepository.getUser(GetUserRequest("user123")) }
        verify { mockProductRepository.getProduct(GetProductRequest("nonexistent")) }
        verify(exactly = 0) { mockPurchaseRepository.createPurchase(any()) }
    }
    
    test("should update purchase status successfully") {
        // Arrange
        val request = UpdateStatusRequest("purchase123", PurchaseStatus.COMPLETED)
        val expectedResponse = UpdateStatusResponse("purchase123", PurchaseStatus.COMPLETED, Instant.now())
        every { mockPurchaseRepository.updatePurchaseStatus(request) } returns expectedResponse
        
        // Act
        val result = purchaseService.updatePurchaseStatus(request)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<UpdateStatusResponse>>()
        result.data shouldBe expectedResponse
        verify { mockPurchaseRepository.updatePurchaseStatus(request) }
    }
    
    test("should return validation error when purchase id is empty for status update") {
        // Arrange
        val request = UpdateStatusRequest("", PurchaseStatus.COMPLETED)
        
        // Act
        val result = purchaseService.updatePurchaseStatus(request)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Purchase ID cannot be empty"
        verify(exactly = 0) { mockPurchaseRepository.updatePurchaseStatus(any()) }
    }
    
    test("should return business rule violation when transitioning from completed to pending") {
        // Arrange
        val request = UpdateStatusRequest("purchase123", PurchaseStatus.PENDING)
        
        // Act
        val result = purchaseService.updatePurchaseStatus(request)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.BusinessRuleViolation>()
        result.error.message shouldBe "Cannot transition to PENDING status from a completed purchase"
        verify(exactly = 0) { mockPurchaseRepository.updatePurchaseStatus(any()) }
    }
    
    test("should retrieve purchase successfully by id") {
        // Arrange
        val purchaseId = "purchase123"
        val expectedPurchase = createMockPurchase()
        val expectedResponse = GetPurchaseResponse(expectedPurchase)
        every { mockPurchaseRepository.getPurchase(GetPurchaseRequest(purchaseId)) } returns expectedResponse
        
        // Act
        val result = purchaseService.getPurchase(purchaseId)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<GetPurchaseResponse>>()
        result.data shouldBe expectedResponse
        verify { mockPurchaseRepository.getPurchase(GetPurchaseRequest(purchaseId)) }
    }
    
    test("should retrieve all purchases for user successfully") {
        // Arrange
        val userId = "user123"
        val expectedPurchases = listOf(createMockPurchase(), createMockPurchase())
        val expectedResponse = GetAllPurchasesForUserResponse(expectedPurchases)
        every { mockPurchaseRepository.getAllPurchasesForUser(GetAllPurchasesForUserRequest(userId)) } returns expectedResponse
        
        // Act
        val result = purchaseService.getAllPurchasesForUser(userId)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<GetAllPurchasesForUserResponse>>()
        result.data shouldBe expectedResponse
        verify { mockPurchaseRepository.getAllPurchasesForUser(GetAllPurchasesForUserRequest(userId)) }
    }
    
    test("should return validation error when user id is empty for get all purchases") {
        // Arrange
        val userId = ""
        
        // Act
        val result = purchaseService.getAllPurchasesForUser(userId)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "User ID cannot be empty"
        verify(exactly = 0) { mockPurchaseRepository.getAllPurchasesForUser(any()) }
    }
    
    test("should calculate total price correctly for multiple items") {
        // Arrange
        val request = CreatePurchaseRequest("user123", "product456", 3, 89.97) // 3 * 29.99
        val userResponse = GetUserResponse("user123", "testuser")
        val productResponse = GetProductResponse("product456", "Test Product", 29.99)
        val expectedResponse = CreatePurchaseResponse("purchase789")
        
        every { mockUserRepository.getUser(GetUserRequest("user123")) } returns userResponse
        every { mockProductRepository.getProduct(GetProductRequest("product456")) } returns productResponse
        every { mockPurchaseRepository.createPurchase(request) } returns expectedResponse
        
        // Act
        val result = purchaseService.createPurchase(request)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<CreatePurchaseResponse>>()
        verify { mockPurchaseRepository.createPurchase(request) }
    }
})

// Helper function to create mock purchase objects
private fun createMockPurchase() = com.example.models.domain.Purchase(
    id = "purchase123",
    userId = "user123",
    productId = "product456",
    quantity = 2,
    totalPrice = 59.98,
    purchaseDate = "2024-01-01T10:00:00",
    lastUpdated = Instant.now(),
    status = PurchaseStatus.PENDING
)