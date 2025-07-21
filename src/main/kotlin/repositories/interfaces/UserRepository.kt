package com.example.repositories.interfaces

import com.example.models.domain.User
import com.example.models.dto.requests.CreateUserRequest
import com.example.models.dto.requests.GetUserRequest
import com.example.models.dto.responses.CreateUserResponse
import com.example.models.dto.responses.GetUserResponse

interface UserRepository {
    fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse
    fun getUser(getUserRequest: GetUserRequest): GetUserResponse
    fun getAllUsers(): List<GetUserResponse>
}