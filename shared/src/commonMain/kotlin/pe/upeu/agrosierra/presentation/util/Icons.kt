package pe.upeu.agrosierra.presentation.util

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Filled.Visibility: ImageVector
    get() {
        if (_visibility != null) return _visibility!!
        _visibility = ImageVector.Builder(
            name = "Filled.Visibility",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12.0f, 4.5f)
                curveTo(7.0f, 4.5f, 2.73f, 7.61f, 1.0f, 12.0f)
                curveTo(2.73f, 16.39f, 7.0f, 19.5f, 12.0f, 19.5f)
                curveTo(17.0f, 19.5f, 21.27f, 16.39f, 23.0f, 12.0f)
                curveTo(21.27f, 7.61f, 17.0f, 4.5f, 12.0f, 4.5f)
                close()
                moveTo(12.0f, 17.0f)
                curveTo(9.24f, 17.0f, 7.0f, 14.76f, 7.0f, 12.0f)
                curveTo(7.0f, 9.24f, 9.24f, 7.0f, 12.0f, 7.0f)
                curveTo(14.76f, 7.0f, 17.0f, 9.24f, 17.0f, 12.0f)
                curveTo(17.0f, 14.76f, 14.76f, 17.0f, 12.0f, 17.0f)
                close()
                moveTo(12.0f, 9.0f)
                curveTo(10.34f, 9.0f, 9.0f, 10.34f, 9.0f, 12.0f)
                curveTo(9.0f, 13.66f, 10.34f, 15.0f, 12.0f, 15.0f)
                curveTo(13.66f, 15.0f, 15.0f, 13.66f, 15.0f, 12.0f)
                curveTo(15.0f, 10.34f, 13.66f, 9.0f, 12.0f, 9.0f)
                close()
            }
        }.build()
        return _visibility!!
    }

private var _visibility: ImageVector? = null

val Icons.Filled.VisibilityOff: ImageVector
    get() {
        if (_visibilityOff != null) return _visibilityOff!!
        _visibilityOff = ImageVector.Builder(
            name = "Filled.VisibilityOff",
            defaultWidth = 24.0.dp,
            defaultHeight = 24.0.dp,
            viewportWidth = 24.0f,
            viewportHeight = 24.0f
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(12.0f, 7.0f)
                curveTo(14.76f, 7.0f, 17.0f, 9.24f, 17.0f, 12.0f)
                curveTo(17.0f, 12.64f, 16.87f, 13.26f, 16.64f, 13.82f)
                lineTo(19.56f, 16.74f)
                curveTo(21.07f, 15.5f, 22.27f, 13.86f, 23.0f, 12.0f)
                curveTo(21.27f, 7.61f, 17.0f, 4.5f, 12.0f, 4.5f)
                curveTo(10.73f, 4.5f, 9.51f, 4.75f, 8.39f, 5.21f)
                lineTo(10.18f, 7.0f)
                curveTo(10.74f, 6.87f, 11.36f, 7.0f, 12.0f, 7.0f)
                close()
                moveTo(2.81f, 2.81f)
                lineTo(1.39f, 4.22f)
                lineTo(4.27f, 7.1f)
                curveTo(2.81f, 8.38f, 1.68f, 10.02f, 1.0f, 12.0f)
                curveTo(2.73f, 16.39f, 7.0f, 19.5f, 12.0f, 19.5f)
                curveTo(13.55f, 19.5f, 15.03f, 19.15f, 16.38f, 18.52f)
                lineTo(19.78f, 21.92f)
                lineTo(21.19f, 20.51f)
                lineTo(2.81f, 2.81f)
                close()
                moveTo(12.0f, 17.0f)
                curveTo(9.24f, 17.0f, 7.0f, 14.76f, 7.0f, 12.0f)
                curveTo(7.0f, 11.12f, 7.23f, 10.3f, 7.63f, 9.58f)
                lineTo(13.42f, 15.37f)
                curveTo(12.98f, 16.37f, 12.0f, 17.0f, 12.0f, 17.0f)
                close()
            }
        }.build()
        return _visibilityOff!!
    }

private var _visibilityOff: ImageVector? = null
