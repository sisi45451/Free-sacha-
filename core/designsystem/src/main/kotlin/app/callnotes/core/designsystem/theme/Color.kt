package app.callnotes.core.designsystem.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Palette de repli, utilisée quand les couleurs dynamiques (tirées du fond
// d'écran, Android 12+) sont désactivées dans les réglages de l'app.
// Teinte de base : un bleu-vert « carnet de notes ».

internal val LightColors = lightColorScheme(
    primary = Color(0xFF00677F),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB7EAFF),
    onPrimaryContainer = Color(0xFF001F28),
    secondary = Color(0xFF4C626B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCFE6F1),
    onSecondaryContainer = Color(0xFF071E26),
    tertiary = Color(0xFF5B5B7E),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE1DFFF),
    onTertiaryContainer = Color(0xFF181837),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    background = Color(0xFFF5FAFD),
    onBackground = Color(0xFF171C1F),
    surface = Color(0xFFF5FAFD),
    onSurface = Color(0xFF171C1F),
    surfaceVariant = Color(0xFFDBE4E8),
    onSurfaceVariant = Color(0xFF40484C),
    outline = Color(0xFF70787C),
)

internal val DarkColors = darkColorScheme(
    primary = Color(0xFF5DD5FC),
    onPrimary = Color(0xFF003543),
    primaryContainer = Color(0xFF004E60),
    onPrimaryContainer = Color(0xFFB7EAFF),
    secondary = Color(0xFFB3CAD4),
    onSecondary = Color(0xFF1E333B),
    secondaryContainer = Color(0xFF354A53),
    onSecondaryContainer = Color(0xFFCFE6F1),
    tertiary = Color(0xFFC4C3EA),
    onTertiary = Color(0xFF2D2D4D),
    tertiaryContainer = Color(0xFF444465),
    onTertiaryContainer = Color(0xFFE1DFFF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    background = Color(0xFF0F1417),
    onBackground = Color(0xFFDEE3E6),
    surface = Color(0xFF0F1417),
    onSurface = Color(0xFFDEE3E6),
    surfaceVariant = Color(0xFF40484C),
    onSurfaceVariant = Color(0xFFBFC8CC),
    outline = Color(0xFF8A9296),
)
