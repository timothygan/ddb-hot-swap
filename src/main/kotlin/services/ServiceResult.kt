package com.example.services

sealed class ServiceResult<out T> {
    data class Success<T>(val data: T) : ServiceResult<T>()
    data class Error(val error: ServiceError) : ServiceResult<Nothing>()
}

sealed class ServiceError(val message: String, val e: Exception) : ServiceResult<ServiceError>() {
    class ValidationError(message: String, e: Exception) : ServiceError(message, e)
    class NotFound(entity: String, id: String, e: Exception) : ServiceError("$entity with id $id not found", e)
    class BusinessRuleViolation(message: String, e: Exception) : ServiceError(message, e)
    class UnknownError(message: String, e: Exception) : ServiceError(message, e)
}