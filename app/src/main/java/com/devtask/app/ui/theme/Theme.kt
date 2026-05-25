package com.devtask.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// --- Colores principales de DevTask ---
val PrimaryBlue = Color(0xFF1A73E8)       // Azul Google / universitario
val PrimaryBlueDark = Color(0xFF4DA3FF)   // Versión clara para modo oscuro
val SecondaryPurple = Color(0xFF7C4DFF)   // Acento morado

// Colores para los niveles de prioridad (se usan en toda la app)
val HighPriorityColor = Color(0xFFE53935)   // Rojo = Alta
val MediumPriorityColor = Color(0xFFFB8C00) // Naranja = Media
val LowPriorityColor = Color(0xFF43A047)    // Verde = Baja

// Esquema de colores para modo claro
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3E3FD),
    onPrimaryContainer = Color(0xFF041E49),
    secondary = SecondaryPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEADDFF),
    onSecondaryContainer = Color(0xFF21005D),
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE8EAF6),
    onSurface = Color(0xFF1C1B1F),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

// Esquema de colores para modo oscuro
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueDark,
    onPrimary = Color(0xFF00347A),
    primaryContainer = Color(0xFF1A4DAC),
    onPrimaryContainer = Color(0xFF041E49),
    secondary = Color(0xFFCFBCFF),
    onSecondary = Color(0xFF381E72),
    secondaryContainer = Color(0xFF4F378B),
    onSecondaryContainer = Color(0xFFEADDFF),
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFF2D2D3A),
    onSurface = Color(0xFFE6E1E5),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99)
)

// Función principal del tema que envuelve toda la app
@Composable
fun DevTaskTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}