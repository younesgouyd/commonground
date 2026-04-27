package com.commonground.client.multiplatform

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.commonground.client.multiplatform.destinations.eventdetails.EventDetails
import com.commonground.client.multiplatform.destinations.eventdetails.EventDetailsNavActions
import com.commonground.client.multiplatform.destinations.eventdetails.EventDetailsViewModel
import com.commonground.client.multiplatform.destinations.home.Home
import com.commonground.client.multiplatform.destinations.home.HomeNavActions
import com.commonground.client.multiplatform.destinations.home.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUi() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    navigationIcon = { NavIcon(navController, scope, drawerState) },
                    title = { Text("CommonGround") }
                )
            }
        ) { padding ->
            ModalNavigationDrawer(
                modifier = Modifier.padding(padding).fillMaxSize(),
                drawerState = drawerState,
                drawerContent = { DrawerSheet { navController.navigate(it.toRoute()) } }
            ) {
                NavHost(navController = navController, startDestination = Route.Home) {
                    composable<Route.Profile> { Text("Profile Screen") }
                    composable<Route.Home> {
                        Home(
                            viewModel = viewModel { HomeViewModel() },
                            navActions = object : HomeNavActions {
                                override fun toEventDetails(id: Long) { navController.navigate(Route.Event(id)) }
                            }
                        )
                    }
                    composable<Route.Friends> { Text("Friends Screen") }
                    composable<Route.Settings> { Text("Settings Screen") }
                    composable<Route.Event> {entry ->
                        val eventRoute = entry.toRoute<Route.Event>()
                        EventDetails(
                            viewModel = viewModel { EventDetailsViewModel(eventRoute.id) },
                            navActions = object : EventDetailsNavActions {
                                override fun toUserProfile(id: Long) { navController.navigate(Route.User(id)) }
                            }
                        )
                    }
                    composable<Route.User> { Text("User Screen") }
                }
            }
        }
    }
}

@Composable
private fun NavIcon(
    navController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val inHome = currentDestination?.hasRoute<Route.Home>() == true && navController.previousBackStackEntry == null
    if (inHome) {
        IconButton(
            onClick = { scope.launch { if (drawerState.isClosed) drawerState.open() else drawerState.close() } },
            content = { Icon(if (drawerState.isClosed) Icons.Default.Menu else Icons.Default.Close, null) }
        )
    } else {
        IconButton(
            onClick = { navController.popBackStack() },
            content = { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        )
    }
}

@Composable
private fun DrawerSheet(
    navigate: (NavigationDrawerItem) -> Unit
) {
    var selectedNavigationDrawerItem by remember { mutableStateOf(NavigationDrawerItem.Home) }

    ModalDrawerSheet {
        Column(
            modifier = Modifier.fillMaxWidth().padding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            NavigationDrawerItem.entries.forEach {
                NavigationDrawerItem(
                    label = { Text(it.name) },
                    selected = it == selectedNavigationDrawerItem,
                    onClick = { selectedNavigationDrawerItem =  it; navigate(it) }
                )
            }
        }
    }
}

private enum class NavigationDrawerItem {
    Profile,
    Home,
    Friends,
    Settings
}

private fun NavigationDrawerItem.toRoute(): Route {
    return when (this) {
        NavigationDrawerItem.Profile -> Route.Profile
        NavigationDrawerItem.Home -> Route.Home
        NavigationDrawerItem.Friends -> Route.Friends
        NavigationDrawerItem.Settings -> Route.Settings
    }
}