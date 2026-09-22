package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.example.data.model.IdeTheme

private val DraculaColorScheme = darkColorScheme(
    primary = DraculaPrimary,
    secondary = DraculaSecondary,
    tertiary = DraculaAccent,
    background = DraculaBackground,
    surface = DraculaSurface,
    onPrimary = DraculaBackground,
    onBackground = DraculaText,
    onSurface = DraculaText
)

private val MonokaiColorScheme = darkColorScheme(
    primary = MonokaiPrimary,
    secondary = MonokaiSecondary,
    tertiary = MonokaiAccent,
    background = MonokaiBackground,
    surface = MonokaiSurface,
    onPrimary = MonokaiBackground,
    onBackground = MonokaiText,
    onSurface = MonokaiText
)

private val OneDarkColorScheme = darkColorScheme(
    primary = OneDarkPrimary,
    secondary = OneDarkSecondary,
    tertiary = OneDarkAccent,
    background = OneDarkBackground,
    surface = OneDarkSurface,
    onPrimary = OneDarkBackground,
    onBackground = OneDarkText,
    onSurface = OneDarkText
)

private val SolarizedColorScheme = darkColorScheme(
    primary = SolarizedPrimary,
    secondary = SolarizedSecondary,
    tertiary = SolarizedAccent,
    background = SolarizedBackground,
    surface = SolarizedSurface,
    onPrimary = SolarizedBackground,
    onBackground = SolarizedText,
    onSurface = SolarizedText
)

private val NordColorScheme = darkColorScheme(
    primary = NordPrimary,
    secondary = NordSecondary,
    tertiary = NordAccent,
    background = NordBackground,
    surface = NordSurface,
    onPrimary = NordBackground,
    onBackground = NordText,
    onSurface = NordText
)

private val LightModernColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightAccent,
    background = LightBackground,
    surface = LightSurface,
    onPrimary = LightSurface,
    onBackground = LightText,
    onSurface = LightText
)

@Composable
fun NoTrackIdeTheme(
    ideTheme: IdeTheme = IdeTheme.DRACULA,
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = when (ideTheme) {
        IdeTheme.DRACULA -> DraculaColorScheme
        IdeTheme.MONOKAI_PRO -> MonokaiColorScheme
        IdeTheme.ONE_DARK -> OneDarkColorScheme
        IdeTheme.SOLARIZED_DARK -> SolarizedColorScheme
        IdeTheme.NORD -> NordColorScheme
        IdeTheme.LIGHT_MODERN -> LightModernColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
