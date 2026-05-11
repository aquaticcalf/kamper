import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val palette = if (isSystemInDarkTheme()) Colors.dark else Colors.light
        val headlineLarge = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 36.sp,
            lineHeight = 40.sp,
        )
        val titleMedium = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        )
        val bodyMedium = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )
        val labelLarge = TextStyle(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            palette.gradientTop,
                            palette.gradientMid,
                            palette.gradientBottom,
                        )
                    )
                )
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize()
            ) {
                    val isWide = maxWidth > 900.dp
                    val contentMaxWidth = if (isWide) 980.dp else 560.dp
                    val heroShapeSize = if (isWide) 320.dp else 220.dp
                    val accentShapeSize = if (isWide) 260.dp else 180.dp

                    Box(
                        modifier = Modifier
                            .size(heroShapeSize)
                            .align(Alignment.TopEnd)
                            .offset(x = if (isWide) 120.dp else 60.dp, y = if (isWide) (-80).dp else (-40).dp)
                            .clip(CircleShape)
                            .background(palette.heroShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(accentShapeSize)
                            .align(Alignment.BottomStart)
                            .offset(x = if (isWide) (-120).dp else (-40).dp, y = if (isWide) 80.dp else 40.dp)
                            .clip(CircleShape)
                            .background(palette.accentShape)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 28.dp, vertical = 32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = contentMaxWidth),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            BasicText(
                                text = "Kamper",
                                style = titleMedium.copy(color = palette.accent),
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            BasicText(
                                text = "A calm, capable starter kit for Kotlin Multiplatform.",
                                style = headlineLarge.copy(color = palette.foreground, textAlign = TextAlign.Center),
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            BasicText(
                                text = "Shared UI, platform-ready structure, and a premium baseline so your next app feels intentional from day one.",
                                style = bodyMedium.copy(
                                    color = palette.foreground.copy(alpha = 0.75f),
                                    textAlign = TextAlign.Center,
                                ),
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(palette.brand)
                                        .clickable(onClick = {
                                            navigator.push(PlannerListScreen())
                                        })
                                        .padding(horizontal = 18.dp, vertical = 10.dp),
                                ) {
                                    BasicText(
                                        text = "Go to Planner",
                                        style = labelLarge.copy(color = palette.brandOn),
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }
}

@Composable
fun StatChip(
    title: String,
    subtitle: String,
    palette: Palette,
    labelLarge: TextStyle,
    bodyMedium: TextStyle,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(palette.brand.copy(alpha = 0.08f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            BasicText(
                text = title,
                style = labelLarge.copy(color = palette.surfaceOn),
            )
            BasicText(
                text = subtitle,
                style = bodyMedium.copy(color = palette.surfaceOn.copy(alpha = 0.6f)),
            )
        }
    }
}
