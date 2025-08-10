import com.example.models.dto.requests.CreateUserRequest
import com.example.models.dto.requests.GetUserRequest
import com.example.repositories.implementations.inmemory.InMemoryUserRepository
import com.example.repositories.interfaces.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.ktor.server.plugins.NotFoundException

class UserRepositoryTest: FunSpec ({
    
    lateinit var repository: UserRepository
    
    beforeEach {
        repository = InMemoryUserRepository()
    }
    
    test("should create user and return generated id") {
        // Arrange
        val createRequest = CreateUserRequest("testuser")
        
        // Act
        val response = repository.createUser(createRequest)
        // Assert
        response.id.shouldNotBeEmpty()
    }
    
    test("should retrieve user by id") {
        // Arrange
        val createRequest = CreateUserRequest("testuser")
        val id = repository.createUser(createRequest).id
        
        // Act
        val getRequest = GetUserRequest(id)
        val getRequestResponse = repository.getUser(getRequest)
        // Assert
        getRequestResponse.id shouldBe id
        getRequestResponse.username shouldBe "testuser"
    }
    
    test("should return all users when multiple exist") {
        // Arrange
        val createRequest = CreateUserRequest("testuser1")
        val createRequest2 = CreateUserRequest("testuser2")
        val createRequest3 = CreateUserRequest("testuser3")
        val id1 = repository.createUser(createRequest).id
        val id2 = repository.createUser(createRequest2).id
        val id3 = repository.createUser(createRequest3).id

        val idToNames = mapOf(
            id1 to "testuser1",
            id2 to "testuser2",
            id3 to "testuser3"
        )

        
        // Act
        val getAllUsersResponse = repository.getAllUsers()
        
        // Assert
        getAllUsersResponse.shouldHaveSize(3)
        getAllUsersResponse.forEach { response ->
            response.id.shouldNotBeEmpty()
            response.username.shouldNotBeEmpty()
            response.username shouldBe idToNames[response.id]
        }
    }
    
    test("should throw exception when user not found") {
        // Arrange
        val getUserRequest = GetUserRequest("nonexistent-id")
        
        // Act & Assert
        shouldThrow<RuntimeException> {
            repository.getUser(getUserRequest)
        }
    }
    
    test("should return empty list when no users exist") {
        // Act
        val getAllUsersResponse = repository.getAllUsers()
        
        // Assert
        getAllUsersResponse.shouldBeEmpty()
    }
})