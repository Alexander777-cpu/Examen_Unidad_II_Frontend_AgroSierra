package pe.upeu.agrosierra.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Sesion(
    val token: String,
    val usuario: String,
    val nombreCompleto: String,
    val roles: List<String> = emptyList(),
    val privilegios: Set<String> = emptySet()
) {
    fun puede(privilegio: String): Boolean {
        return privilegios.contains(privilegio)
    }
}
