import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.material.icons.filled.Edit

class PlannerDetailScreen(private val planId: Long) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val database = remember { provideDatabase() }
        var plan by remember { mutableStateOf<Plan?>(null) }
        val palette = if (isSystemInDarkTheme()) Colors.dark else Colors.light

        LaunchedEffect(planId) {
            plan = database.planDao().getPlanById(planId)
        }

        val titleStyle = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            color = palette.foreground
        )

        Scaffold(
            floatingActionButton = {
                if (plan != null) {
                    FloatingActionButton(
                        onClick = { navigator.push(PlannerCreateScreen(planId)) },
                        containerColor = palette.brand,
                        contentColor = palette.brandOn,
                        shape = RoundedCornerShape(18.dp),
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Plan")
                    }
                }
            },
            containerColor = androidx.compose.ui.graphics.Color.Transparent
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(palette.gradientTop, palette.gradientMid, palette.gradientBottom)
                        )
                    )
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isWide = maxWidth > 900.dp
                    val heroShapeSize = if (isWide) 320.dp else 220.dp
                    val accentShapeSize = if (isWide) 260.dp else 180.dp

                    Box(
                        modifier = Modifier
                            .size(heroShapeSize)
                            .align(Alignment.TopEnd)
                            .offset(x = if (isWide) 120.dp else 60.dp, y = if (isWide) (-80).dp else (-40).dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(palette.heroShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(accentShapeSize)
                            .align(Alignment.BottomStart)
                            .offset(x = if (isWide) (-120).dp else (-40).dp, y = if (isWide) 80.dp else 40.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(palette.accentShape)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 28.dp, vertical = 32.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(palette.surface.copy(alpha = 0.5f))
                                    .clickable { navigator.pop() }
                                    .padding(10.dp),
                                tint = palette.foreground
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            BasicText(text = "Plan Details", style = titleStyle)
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        plan?.let { p ->
                            Column {
                                BasicText(
                                    text = p.title,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Serif,
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.foreground
                                    )
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(palette.brand.copy(alpha = 0.08f))
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    BasicText(
                                        text = "Due on ${formatDate(p.deadline)}",
                                        style = TextStyle(
                                            fontFamily = FontFamily.SansSerif,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = palette.brand
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(40.dp))
                                BasicText(
                                    text = "Description",
                                    style = TextStyle(
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.foreground.copy(alpha = 0.4f)
                                    )
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                BasicText(
                                    text = p.description.ifEmpty { "No description provided." },
                                    style = TextStyle(
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 16.sp,
                                        lineHeight = 26.sp,
                                        color = palette.foreground.copy(alpha = 0.8f)
                                    )
                                )
                            }
                        } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = palette.brand)
                        }
                    }
                }
            }
        }
    }
}
