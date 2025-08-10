import com.example.models.dto.requests.CreateUserRequest
import com.example.models.dto.requests.GetUserRequest
import com.example.repositories.implementations.inmemory.InMemoryUserRepository
import com.example.repositories.interfaces.UserRepository
import io.kotest.core.spec.style.FunSpec

class UserRepositoryTest: FunSpec ({
    
    lateinit var repository: UserRepository
    
    beforeEach {
        repository = InMemoryUserRepository()
    }
    
    test("should create user and return generated id") {
        // Arrange
        val createRequest = CreateUserRequest("testuser")
        
        // Act
        // TODO: Implement test
        
        // Assert
        // TODO: Add assertions
    }
    
    test("should retrieve user by id") {
        // Arrange
        // TODO: Create user first
        
        // Act
        // TODO: Get user by id
        
        // Assert
        // TODO: Verify user details
    }
    
    test("should return all users when multiple exist") {
        // Arrange
        // TODO: Create multiple users
        
        // Act
        // TODO: Get all users
        
        // Assert
        // TODO: Verify count and contents
    }
    
    test("should throw exception when user not found") {
        // Arrange
        val getUserRequest = GetUserRequest("nonexistent-id")
        
        // Act & Assert
        // TODO: Verify exception is thrown
    }
    
    test("should return empty list when no users exist") {
        // Act
        // TODO: Get all users from empty repository
        
        // Assert
        // TODO: Verify empty list
    }
})