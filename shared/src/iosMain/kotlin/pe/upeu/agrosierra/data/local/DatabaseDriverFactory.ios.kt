package pe.upeu.agrosierra.data.local

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        throw UnsupportedOperationException("iOS SqlDriver not configured")
    }
}
