import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.foundation.isSystemInDarkTheme

import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.ExperimentalComposeUiApi

class PlannerListScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val database = remember { provideDatabase() }
        val plans by database.planDao().getAllPlans().collectAsState(initial = emptyList())
        val palette = if (isSystemInDarkTheme()) Colors.dark else Colors.light

        var planToDelete by remember { mutableStateOf<Plan?>(null) }

        if (planToDelete != null) {
            AlertDialog(
                onDismissRequest = { planToDelete = null },
                confirmButton = {
                    TextButton(onClick = {
                        val p = planToDelete
                        if (p != null) {
                            scope.launch {
                                database.planDao().delete(p)
                                planToDelete = null
                            }
                        }
                    }) {
                        Text("Delete", color = Color.Red, style = TextStyle(fontWeight = FontWeight.Bold))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { planToDelete = null }) {
                        Text("Cancel", color = palette.foreground.copy(alpha = 0.6f))
                    }
                },
                title = {
                    Text(
                        "Delete Plan?",
                        style = TextStyle(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to delete \"${planToDelete?.title}\"? This action cannot be undone.",
                        style = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp)
                    )
                },
                containerColor = palette.surface,
                textContentColor = palette.foreground,
                titleContentColor = palette.foreground,
                shape = RoundedCornerShape(24.dp)
            )
        }

        val titleStyle = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            color = palette.foreground
        )

        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navigator.push(PlannerCreateScreen()) },
                    containerColor = palette.brand,
                    contentColor = palette.brandOn,
                    shape = RoundedCornerShape(18.dp),
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Plan")
                }
            },
            containerColor = Color.Transparent
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
                            BasicText(text = "My Plans", style = titleStyle)
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        if (plans.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize().padding(bottom = 100.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    BasicText(
                                        text = "Your planner is empty",
                                        style = TextStyle(
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = palette.foreground.copy(alpha = 0.6f)
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    BasicText(
                                        text = "Tap the + button to create your first plan.",
                                        style = TextStyle(
                                            fontFamily = FontFamily.SansSerif,
                                            fontSize = 14.sp,
                                            color = palette.foreground.copy(alpha = 0.4f)
                                        )
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(bottom = 80.dp)
                            ) {
                                items(plans) { plan ->
                                    PlanItem(
                                        plan = plan,
                                        palette = palette,
                                        onClick = {
                                            navigator.push(PlannerDetailScreen(plan.id))
                                        },
                                        onEdit = {
                                            navigator.push(PlannerCreateScreen(plan.id))
                                        },
                                        onDelete = {
                                            planToDelete = plan
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PlanItem(plan: Plan, palette: Palette, onClick: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }
    var menuOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(palette.surface.copy(alpha = 0.6f))
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { offset ->
                        menuOffset = offset
                        showMenu = true
                    },
                    onTap = { onClick() }
                )
            }
            .padding(24.dp)
    ) {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                BasicText(
                    text = plan.title,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = palette.foreground
                    ),
                    modifier = Modifier.weight(1f)
                )
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(palette.brand.copy(alpha = 0.08f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    BasicText(
                        text = formatDate(plan.deadline),
                        style = TextStyle(
                            fontFamily = FontFamily.SansSerif,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = palette.brand
                        )
                    )
                }
            }
            if (plan.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                BasicText(
                    text = plan.description,
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = palette.foreground.copy(alpha = 0.6f)
                    ),
                    maxLines = 2
                )
            }
        }

        val density = androidx.compose.ui.platform.LocalDensity.current
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            offset = with(density) { DpOffset(menuOffset.x.toDp(), menuOffset.y.toDp() - 48.dp) },
            containerColor = palette.surface,
            shape = RoundedCornerShape(16.dp),
        ) {
            DropdownMenuItem(
                text = { Text("Edit", color = palette.foreground, style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium)) },
                onClick = {
                    onEdit()
                    showMenu = false
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            )
            DropdownMenuItem(
                text = { Text("Delete", color = Color.Red, style = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium)) },
                onClick = {
                    onDelete()
                    showMenu = false
                },
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}
