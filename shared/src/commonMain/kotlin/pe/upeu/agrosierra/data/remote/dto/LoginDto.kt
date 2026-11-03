package pe.upeu.agrosierra.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    val usuario: String,
    val clave: String
)

@Serializable
data class LoginResponseDto(
    val token: String,
    val usuario: String,
    val nombreCompleto: String,
    val roles: List<String> = emptyList(),
    val privilegios: List<String> = emptyList()
)
