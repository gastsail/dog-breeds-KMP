import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.CrossConfigDevice
import data.SessionCache
import data.TitleTopBarTypes
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.Navigator
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.photo
import navigation.Navigation
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinContext

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App(configDevice: CrossConfigDevice? = null) {

    PreComposeApp {
        KoinContext {
            val colors = getColorsTheme()
            SessionCache.configDevice = configDevice

            AppTheme {
                val navigator = rememberNavigator()
                val titleTopBar = getTitleTopAppBar(navigator)
                val isEditOrAddExpenses = titleTopBar != TitleTopBarTypes.DASHBOARD.value
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            elevation = 0.dp,
                            title = {
                                Text(
                                    color = colors.textColor,
                                    text = titleTopBar, fontSize = 25.sp
                                )
                            },
                            navigationIcon = {
                                if (isEditOrAddExpenses) {
                                    IconButton(
                                        onClick = {
                                            navigator.popBackStack()
                                        }) {
                                        Icon(
                                            modifier = Modifier.padding(start = 16.dp),
                                            imageVector = Icons.Default.ArrowBack,
                                            tint = colors.textColor,
                                            contentDescription = null
                                        )
                                    }
                                } else {
                                    Icon(
                                        modifier = Modifier.padding(start = 16.dp),
                                        imageVector = Icons.Default.Apps,
                                        tint = colors.textColor,
                                        contentDescription = null
                                    )
                                }
                            },
                            actions = {
                                if (!isEditOrAddExpenses) {
                                    Row(
                                        modifier = Modifier.padding(end = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            modifier = Modifier.size(40.dp)
                                                .clip(RoundedCornerShape(45)),
                                            painter = painterResource(Res.drawable.photo),
                                            contentDescription = null
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Hello, Gastón", color = colors.textColor)
                                    }

                                }
                            },
                            backgroundColor = colors.backgroundColor
                        )
                    },
                    floatingActionButton = {
                        if (!isEditOrAddExpenses) {
                            FloatingActionButton(
                                modifier = Modifier.padding(8.dp),
                                onClick = {
                                    navigator.navigate("/addExpenses")
                                },
                                shape = RoundedCornerShape(50),
                                backgroundColor = colors.addIconColor,
                                contentColor = Color.White
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    tint = Color.White,
                                    contentDescription = null
                                )
                            }
                        }
                    }) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Navigation(navigator)
                    }
                }
            }
        }
    }
}

@Composable
fun getTitleTopAppBar(navigator: Navigator): String {
    var titleTopBar = TitleTopBarTypes.DASHBOARD

    val isOnAddExpenses =
        navigator.currentEntry.collectAsState(null).value?.route?.route.equals("/addExpenses/{id}?")
    if (isOnAddExpenses) {
        titleTopBar = TitleTopBarTypes.ADD
    }

    val isOnEditExpense = navigator.currentEntry.collectAsState(null).value?.path<Long>("id")
    isOnEditExpense?.let {
        titleTopBar = TitleTopBarTypes.EDIT
    }

    return titleTopBar.value
}