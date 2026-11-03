package pe.upeu.agrosierra.data.local

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import pe.upeu.agrosierra.db.AgroDb

actual class DatabaseDriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(AgroDb.Schema, context, "agro_database.db")
    }
}
