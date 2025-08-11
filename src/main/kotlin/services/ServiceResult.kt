package com.example.services

sealed class ServiceResult<out T> {
    data class Success<T>(val data: T) : ServiceResult<T>()
    data class Error(val error: ServiceError) : ServiceResult<Nothing>()
}

sealed class ServiceError(val message: String) {
    class ValidationError(message: String) : ServiceError(message)
    class NotFound(entity: String, id: String) : ServiceError("$entity with id $id not found")
    class BusinessRuleViolation(message: String) : ServiceError(message)
    class UnknownError(message: String) : ServiceError(message)
}