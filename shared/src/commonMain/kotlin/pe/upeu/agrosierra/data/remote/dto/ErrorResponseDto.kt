package pe.upeu.agrosierra.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseDto(
    val timestamp: String? = null,
    val status: Int = 0,
    val error: String? = null,
    val message: String? = null,
    val path: String? = null,
    val validationErrors: Map<String, String>? = null
)
