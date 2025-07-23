package com.example.repositories.implementations.inmemory

import com.example.models.domain.User
import com.example.models.dto.requests.CreateUserRequest
import com.example.models.dto.requests.GetUserRequest
import com.example.models.dto.responses.CreateUserResponse
import com.example.models.dto.responses.GetUserResponse
import com.example.repositories.interfaces.UserRepository
import java.util.UUID

class InMemoryUserRepository: UserRepository {
    private val store: MutableMap<String, User> = mutableMapOf()

    override fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse =
        UUID
            .randomUUID()
            .toString()
            .let {
                val newUser = User(
                    it,
                    createUserRequest.username,
                )
                store.putIfAbsent(it, newUser)
                CreateUserResponse(it)
            }

    override fun getUser(getUserRequest: GetUserRequest): GetUserResponse =
        store[getUserRequest.id]
            ?.let {
                GetUserResponse(
                    it.username
                )
            } ?: throw RuntimeException("User not found")

    override fun getAllUsers(): List<GetUserResponse> =
        store
            .map {
                (GetUserResponse(it.value.username))
            }
}