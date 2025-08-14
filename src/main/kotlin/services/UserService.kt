package com.example.services

import com.example.models.dto.requests.CreateUserRequest
import com.example.models.dto.requests.GetUserRequest
import com.example.models.dto.responses.CreateUserResponse
import com.example.models.dto.responses.GetUserResponse
import com.example.repositories.interfaces.UserRepository

class UserService(
    private val userRepository: UserRepository
) {
    fun createUser(username: String): ServiceResult<CreateUserResponse> =
        try {
            validateCreateUserRequest(
                username
            )?.let { err ->
                ServiceResult.Error(err)
            } ?: userRepository
                .createUser(
                    CreateUserRequest(
                        username,
                    )
                )
                .let {
                    ServiceResult.Success(it)
                }

        } catch (e: Exception) {
            ServiceResult.Error(
                ServiceError.UnknownError(
                    e.message ?: e.javaClass.simpleName,
                    e
                )
            )
        }

    
    fun getUser(userId: String): ServiceResult<GetUserResponse> =
        try {
            validateUserId(userId)?.let { err ->
                ServiceResult.Error(err)
            } ?: userRepository
                .getUser(
                    GetUserRequest(
                        userId,
                    )
                ).let {
                    ServiceResult.Success(it)
                }

        } catch (e: Exception) {
            when (e) {
                is RuntimeException -> ServiceResult.Error(
                    ServiceError.NotFound("User", userId, e)
                    )

                else -> ServiceResult.Error(
                    ServiceError.UnknownError(
                        e.message ?: e.javaClass.simpleName,
                        e
                    )
                )
            }
        }
    
    fun getAllUsers(): ServiceResult<List<GetUserResponse>> =
        try {
            userRepository
                .getAllUsers()
                .let { users ->
                    ServiceResult.Success(users)
                }
        } catch (e: Exception) {
            ServiceResult.Error(
                ServiceError.UnknownError(
                    e.message ?: e.javaClass.simpleName, e
                )
            )
        }

    
    private fun validateCreateUserRequest(username: String): ServiceError? =
        when {
            username.isEmpty() -> ServiceError
                .ValidationError(
                    "Username cannot be empty",
                    Exception()
                )

            username.isBlank() -> ServiceError
                .ValidationError(
                    "Username cannot be empty",
                    Exception()
                )

            username.length > 50 -> ServiceError
                .ValidationError(
                    "Username cannot exceed 50 characters",
                    Exception()
                )

            else -> null
        }
    
    private fun validateUserId(userId: String): ServiceError? =
        if (userId.isBlank() || userId.isEmpty()) ServiceError.ValidationError("User ID cannot be empty", Exception())
        else null
}