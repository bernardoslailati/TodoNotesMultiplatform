import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import data.MongoDb
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.context.startKoin
import org.koin.dsl.module
import presentation.screen.home.HomeScreen
import presentation.screen.home.HomeViewModel
import presentation.screen.task.TaskViewModel

val lightPink = Color(0xFFda3895)
val lightGreen = Color(0xFFbbd630)

val darkOrange = Color(0xFFea4424)
val lightBabyBlue = Color(0xFF45b8b2)

@Composable
@Preview
fun App() {
    initializeKoin()

    val lightColors = lightColorScheme(
        primary = lightPink,
        onPrimary = lightGreen,
        primaryContainer = lightPink,
        onPrimaryContainer = lightGreen
    )
    val darkColors = lightColorScheme(
        primary = darkOrange,
        onPrimary = lightBabyBlue,
        primaryContainer = darkOrange,
        onPrimaryContainer = lightBabyBlue
    )
    val colors = if (isSystemInDarkTheme()) darkColors else lightColors

    MaterialTheme(colorScheme = colors) {
        Navigator(HomeScreen()) {
            SlideTransition(it)
        }
    }
}

val mongoDbModule = module {
    single<MongoDb> { MongoDb() }
    factory<HomeViewModel> { HomeViewModel(get()) }
    factory<TaskViewModel> { TaskViewModel(get()) }
}

fun initializeKoin() {
    startKoin {
        modules(mongoDbModule)
    }
}