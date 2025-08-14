import com.example.models.dto.requests.CreateUserRequest
import com.example.models.dto.requests.GetUserRequest
import com.example.models.dto.responses.CreateUserResponse
import com.example.models.dto.responses.GetUserResponse
import com.example.repositories.interfaces.UserRepository
import com.example.services.ServiceError
import com.example.services.ServiceResult
import com.example.services.UserService
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*

class UserServiceTest : FunSpec({
    
    lateinit var mockUserRepository: UserRepository
    lateinit var userService: UserService
    
    beforeEach {
        mockUserRepository = mockk()
        userService = UserService(mockUserRepository)
    }
    
    afterEach {
        clearAllMocks()
    }
    
    test("should create user successfully when valid request") {
        // Arrange
        val username = "validuser"
        val request = CreateUserRequest(username)
        val expectedResponse = CreateUserResponse("user123")
        every { mockUserRepository.createUser(request) } returns expectedResponse
        
        // Act
        val result = userService.createUser(username)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<CreateUserResponse>>()
        result.data shouldBe expectedResponse
        verify { mockUserRepository.createUser(request) }
    }
    
    test("should return validation error when username is empty") {
        // Arrange
        val username = ""
        
        // Act
        val result = userService.createUser(username)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Username cannot be empty"
        verify(exactly = 0) { mockUserRepository.createUser(any()) }
    }
    
    test("should return validation error when username is blank") {
        // Arrange
        val username = "   "
        
        // Act
        val result = userService.createUser(username)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Username cannot be empty"
        verify(exactly = 0) { mockUserRepository.createUser(any()) }
    }
    
    test("should return validation error when username is too long") {
        // Arrange
        val longUsername = "a".repeat(51) // Assuming 50 char limit

        // Act
        val result = userService.createUser(longUsername)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "Username cannot exceed 50 characters"
        verify(exactly = 0) { mockUserRepository.createUser(any()) }
    }
    
    test("should handle repository exceptions gracefully") {
        // Arrange
        val username = "validuser"
        val request = CreateUserRequest(username)
        every { mockUserRepository.createUser(request) } throws RuntimeException("Database error")
        
        // Act
        val result = userService.createUser(username)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.UnknownError>()
        result.error.message shouldBe "Database error"
        verify { mockUserRepository.createUser(request) }
    }
    
    test("should retrieve user successfully by id") {
        // Arrange
        val userId = "user123"
        val expectedResponse = GetUserResponse("user123", "testuser")
        every { mockUserRepository.getUser(GetUserRequest(userId)) } returns expectedResponse
        
        // Act
        val result = userService.getUser(userId)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<GetUserResponse>>()
        result.data shouldBe expectedResponse
        verify { mockUserRepository.getUser(GetUserRequest(userId)) }
    }
    
    test("should return validation error when userId is empty") {
        // Arrange
        val userId = ""
        
        // Act
        val result = userService.getUser(userId)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.ValidationError>()
        result.error.message shouldBe "User ID cannot be empty"
        verify(exactly = 0) { mockUserRepository.getUser(any()) }
    }
    
    test("should return not found error when user does not exist") {
        // Arrange
        val userId = "nonexistent"
        every { mockUserRepository.getUser(GetUserRequest(userId)) } throws RuntimeException("User not found")
        
        // Act
        val result = userService.getUser(userId)
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Error>()
        result.error.shouldBeInstanceOf<ServiceError.NotFound>()
        result.error.message shouldBe "User with id nonexistent not found"
        verify { mockUserRepository.getUser(GetUserRequest(userId)) }
    }
    
    test("should retrieve all users successfully") {
        // Arrange
        val expectedUsers = listOf(
            GetUserResponse("user1", "testuser1"),
            GetUserResponse("user2", "testuser2")
        )
        every { mockUserRepository.getAllUsers() } returns expectedUsers
        
        // Act
        val result = userService.getAllUsers()
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<List<GetUserResponse>>>()
        result.data shouldBe expectedUsers
        verify { mockUserRepository.getAllUsers() }
    }
    
    test("should return empty list when no users exist") {
        // Arrange
        every { mockUserRepository.getAllUsers() } returns emptyList()
        
        // Act
        val result = userService.getAllUsers()
        
        // Assert
        result.shouldBeInstanceOf<ServiceResult.Success<List<GetUserResponse>>>()
        result.data shouldBe emptyList()
        verify { mockUserRepository.getAllUsers() }
    }
})