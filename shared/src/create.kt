import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.*
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
import androidx.compose.foundation.verticalScroll
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch
import androidx.compose.foundation.isSystemInDarkTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
class PlannerCreateScreen(private val planId: Long? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val database = remember { provideDatabase() }
        val palette = if (isSystemInDarkTheme()) Colors.dark else Colors.light
        val scrollState = androidx.compose.foundation.rememberScrollState()

        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var deadline by remember { mutableStateOf(Clock.System.now()) }
        var showDatePicker by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(planId != null) }

        LaunchedEffect(planId) {
            if (planId != null) {
                val plan = database.planDao().getPlanById(planId)
                if (plan != null) {
                    title = plan.title
                    description = plan.description
                    deadline = Instant.fromEpochMilliseconds(plan.deadline)
                }
                isLoading = false
            }
        }

        val titleStyle = TextStyle(
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            color = palette.foreground
        )

        Scaffold(
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

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = palette.brand)
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(horizontal = 28.dp, vertical = 32.dp)
                                .verticalScroll(scrollState)
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
                                BasicText(text = if (planId == null) "New Plan" else "Edit Plan", style = titleStyle)
                            }

                            Spacer(modifier = Modifier.height(40.dp))

                            InputField("Title", title, { title = it }, palette)
                            Spacer(modifier = Modifier.height(24.dp))
                            InputField("Description", description, { description = it }, palette, singleLine = false, minHeight = 160.dp)
                            Spacer(modifier = Modifier.height(24.dp))

                            Column {
                                BasicText(
                                    "Deadline",
                                    style = TextStyle(
                                        fontFamily = FontFamily.SansSerif,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = palette.foreground.copy(alpha = 0.5f)
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(palette.surface.copy(alpha = 0.6f))
                                        .clickable { showDatePicker = true }
                                        .padding(20.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.DateRange, contentDescription = null, tint = palette.brand, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        BasicText(
                                            formatDate(deadline.toEpochMilliseconds()),
                                            style = TextStyle(
                                                fontFamily = FontFamily.SansSerif,
                                                fontSize = 16.sp,
                                                color = palette.foreground
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = {
                                    if (title.isNotBlank()) {
                                        scope.launch {
                                            val p = Plan(
                                                id = planId ?: 0,
                                                title = title,
                                                description = description,
                                                deadline = deadline.toEpochMilliseconds()
                                            )
                                            if (planId == null) {
                                                database.planDao().insert(p)
                                            } else {
                                                database.planDao().update(p)
                                            }
                                            navigator.pop()
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = palette.brand, contentColor = palette.brandOn),
                                shape = RoundedCornerShape(18.dp),
                                enabled = title.isNotBlank(),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp, pressedElevation = 0.dp)
                            ) {
                                Text(
                                    if (planId == null) "Save Plan" else "Update Plan",
                                    style = TextStyle(
                                        fontFamily = FontFamily.SansSerif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = deadline.toEpochMilliseconds())
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            deadline = Instant.fromEpochMilliseconds(millis)
                        }
                        showDatePicker = false
                    }) {
                        Text("OK", color = palette.brand, style = TextStyle(fontWeight = FontWeight.Bold))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel", color = palette.foreground.copy(alpha = 0.6f))
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = palette.surface,
                )
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = palette.surface,
                        titleContentColor = palette.foreground,
                        headlineContentColor = palette.foreground,
                        weekdayContentColor = palette.foreground.copy(alpha = 0.6f),
                        subheadContentColor = palette.foreground.copy(alpha = 0.6f),
                        yearContentColor = palette.foreground,
                        currentYearContentColor = palette.brand,
                        selectedYearContentColor = palette.brandOn,
                        selectedYearContainerColor = palette.brand,
                        dayContentColor = palette.foreground,
                        selectedDayContainerColor = palette.brand,
                        selectedDayContentColor = palette.brandOn,
                        todayContentColor = palette.brand,
                        todayDateBorderColor = palette.brand,
                    )
                )
            }
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    palette: Palette,
    singleLine: Boolean = true,
    minHeight: androidx.compose.ui.unit.Dp = 56.dp
) {
    Column {
        BasicText(
            label,
            style = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = palette.foreground.copy(alpha = 0.5f)
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minHeight),
            textStyle = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontSize = 16.sp,
                color = palette.foreground
            ),
            placeholder = {
                Text(
                    "Enter $label...",
                    style = TextStyle(
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 16.sp,
                        color = palette.foreground.copy(alpha = 0.25f)
                    )
                )
            },
            singleLine = singleLine,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = palette.surface.copy(alpha = 0.8f),
                unfocusedContainerColor = palette.surface.copy(alpha = 0.6f),
                focusedBorderColor = palette.brand,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = palette.brand,
                focusedLabelColor = palette.brand,
            )
        )
    }
}

@Suppress("DEPRECATION")
fun formatDate(millis: Long): String {
    val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return "${dateTime.dayOfMonth} ${monthNames[dateTime.month.ordinal]} ${dateTime.year}"
}
