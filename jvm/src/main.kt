import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition

fun main() = application {
    val windowState = rememberWindowState(width = 1000.dp, height = 800.dp)
    Window(
        onCloseRequest = ::exitApplication,
        title = Config.NAME,
        state = windowState
    ) {
        Navigator(HomeScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}
