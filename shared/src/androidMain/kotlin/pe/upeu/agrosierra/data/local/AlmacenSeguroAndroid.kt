package pe.upeu.agrosierra.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import pe.upeu.agrosierra.domain.model.Sesion

class AlmacenSeguroAndroid(
    private val context: Context,
    private val json: Json = Json { ignoreUnknownKeys = true; isLenient = true }
) : AlmacenSeguro {

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val sharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "agrosierra_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override suspend fun guardarToken(token: String): Unit = withContext(Dispatchers.IO) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    override suspend fun leerToken(): String? = withContext(Dispatchers.IO) {
        sharedPreferences.getString("auth_token", null)
    }

    override suspend fun borrarToken(): Unit = withContext(Dispatchers.IO) {
        sharedPreferences.edit().remove("auth_token").remove("sesion_data").apply()
    }

    override suspend fun guardarSesion(sesion: Sesion): Unit = withContext(Dispatchers.IO) {
        val jsonStr = json.encodeToString(Sesion.serializer(), sesion)
        sharedPreferences.edit()
            .putString("auth_token", sesion.token)
            .putString("sesion_data", jsonStr)
            .apply()
    }

    override suspend fun leerSesion(): Sesion? = withContext(Dispatchers.IO) {
        val jsonStr = sharedPreferences.getString("sesion_data", null) ?: return@withContext null
        runCatching {
            json.decodeFromString(Sesion.serializer(), jsonStr)
        }.getOrNull()
    }
}
