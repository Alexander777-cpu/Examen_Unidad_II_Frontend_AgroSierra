package pe.upeu.agrosierra.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("username") val usuario: String,
    @SerialName("password") val clave: String
)

@Serializable
data class LoginResponseDto(
    val token: String,
    @SerialName("username") val usuario: String = "",
    val nombreCompleto: String = "",
    val roles: List<String> = emptyList(),
    val privilegios: List<String> = emptyList()
)
