import com.example.models.domain.PurchaseStatus
import com.example.models.dto.requests.CreatePurchaseRequest
import com.example.models.dto.requests.GetAllPurchasesForUserRequest
import com.example.models.dto.requests.GetPurchaseRequest
import com.example.models.dto.requests.UpdateStatusRequest
import com.example.repositories.implementations.inmemory.InMemoryPurchaseRepository
import com.example.repositories.interfaces.PurchaseRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty

class PurchaseRepositoryTest : FunSpec({
    
    lateinit var repository: PurchaseRepository
    
    beforeEach {
        repository = InMemoryPurchaseRepository()
    }
    
    test("should create purchase and return generated id") {
        // Arrange
        val createRequest = CreatePurchaseRequest(
            userId = "user123",
            productId = "product456", 
            quantity = 2,
            totalPrice = 29.99
        )
        
        // Act
        val response = repository.createPurchase(createRequest)
        
        // Assert
        response.id.shouldNotBeEmpty()
    }
    
    test("should retrieve purchase by id") {
        // Arrange
        val createRequest = CreatePurchaseRequest("user123", "product456", 2, 59.98)
        val purchaseId = repository.createPurchase(createRequest).id
        
        // Act
        val getRequest = GetPurchaseRequest(purchaseId)
        val response = repository.getPurchase(getRequest)
        
        // Assert
        response.purchase.id shouldBe purchaseId
        response.purchase.userId shouldBe "user123"
        response.purchase.productId shouldBe "product456"
        response.purchase.quantity shouldBe 2
        response.purchase.status shouldBe PurchaseStatus.PENDING
        response.purchase.totalPrice shouldBe 59.98
        response.purchase.purchaseDate.shouldNotBeEmpty()
    }
    
    test("should update purchase status") {
        // Arrange
        val createRequest = CreatePurchaseRequest("user123", "product456", 1, 15.99)
        val purchaseId = repository.createPurchase(createRequest).id
        val updateRequest = UpdateStatusRequest(purchaseId, PurchaseStatus.COMPLETED)
        
        // Act
        val updateResponse = repository.updatePurchaseStatus(updateRequest)
        
        // Assert
        updateResponse.purchaseId shouldBe purchaseId
        updateResponse.newPurchaseStatus shouldBe PurchaseStatus.COMPLETED
        
        // Verify the purchase was actually updated
        val getResponse = repository.getPurchase(GetPurchaseRequest(purchaseId))
        getResponse.purchase.status shouldBe PurchaseStatus.COMPLETED
    }
    
    test("should get all purchases for user") {
        // Arrange
        val userId = "user123"
        val purchase1Id = repository.createPurchase(CreatePurchaseRequest(userId, "product1", 1, 10.0)).id
        val purchase2Id = repository.createPurchase(CreatePurchaseRequest(userId, "product2", 2, 20.0)).id
        val purchase3Id = repository.createPurchase(CreatePurchaseRequest("otherUser", "product3", 1, 30.0)).id
        
        // Act
        val response = repository.getAllPurchasesForUser(GetAllPurchasesForUserRequest(userId))
        
        // Assert
        response.purchases.shouldHaveSize(2)
        val purchaseIds = response.purchases.map { it.id }
        purchaseIds shouldBe listOf(purchase1Id, purchase2Id)
    }
    
    test("should return empty list for user with no purchases") {
        // Act
        val response = repository.getAllPurchasesForUser(GetAllPurchasesForUserRequest("nonexistentUser"))
        
        // Assert
        response.purchases.shouldBeEmpty()
    }
    
    test("should throw exception when getting non-existent purchase") {
        // Arrange
        val getRequest = GetPurchaseRequest("nonexistent-id")
        
        // Act & Assert
        shouldThrow<RuntimeException> {
            repository.getPurchase(getRequest)
        }
    }
    
    test("should throw exception when updating status of non-existent purchase") {
        // Arrange
        val updateRequest = UpdateStatusRequest("nonexistent-id", PurchaseStatus.CANCELLED)
        
        // Act & Assert
        shouldThrow<RuntimeException> {
            repository.updatePurchaseStatus(updateRequest)
        }
    }
    
    test("should handle multiple status updates") {
        // Arrange
        val purchaseId = repository.createPurchase(CreatePurchaseRequest("user123", "product456", 1, 25.0)).id
        
        // Act - Update status multiple times
        repository.updatePurchaseStatus(UpdateStatusRequest(purchaseId, PurchaseStatus.COMPLETED))
        repository.updatePurchaseStatus(UpdateStatusRequest(purchaseId, PurchaseStatus.REFUNDED))
        
        // Assert
        val finalPurchase = repository.getPurchase(GetPurchaseRequest(purchaseId))
        finalPurchase.purchase.status shouldBe PurchaseStatus.REFUNDED
    }
    
    test("should handle different purchase statuses") {
        // Arrange & Act
        val pendingId = repository.createPurchase(CreatePurchaseRequest("user1", "product1", 1, 10.0)).id
        val completedId = repository.createPurchase(CreatePurchaseRequest("user1", "product2", 1, 20.0)).id
        val cancelledId = repository.createPurchase(CreatePurchaseRequest("user1", "product3", 1, 30.0)).id
        
        repository.updatePurchaseStatus(UpdateStatusRequest(completedId, PurchaseStatus.COMPLETED))
        repository.updatePurchaseStatus(UpdateStatusRequest(cancelledId, PurchaseStatus.CANCELLED))
        
        // Assert
        repository.getPurchase(GetPurchaseRequest(pendingId)).purchase.status shouldBe PurchaseStatus.PENDING
        repository.getPurchase(GetPurchaseRequest(completedId)).purchase.status shouldBe PurchaseStatus.COMPLETED
        repository.getPurchase(GetPurchaseRequest(cancelledId)).purchase.status shouldBe PurchaseStatus.CANCELLED
    }
})