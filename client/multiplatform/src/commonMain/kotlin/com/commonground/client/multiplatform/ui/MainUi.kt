package com.commonground.client.multiplatform.ui

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
import com.commonground.client.multiplatform.data.RepoStore
import com.commonground.client.multiplatform.ui.destinations.eventdetails.EventDetails
import com.commonground.client.multiplatform.ui.destinations.eventdetails.EventDetailsNavActions
import com.commonground.client.multiplatform.ui.destinations.eventdetails.EventDetailsViewModel
import com.commonground.client.multiplatform.ui.destinations.home.Home
import com.commonground.client.multiplatform.ui.destinations.home.HomeNavActions
import com.commonground.client.multiplatform.ui.destinations.home.HomeViewModel
import com.commonground.client.multiplatform.ui.destinations.login.Login
import com.commonground.client.multiplatform.ui.destinations.login.LoginNavActions
import com.commonground.client.multiplatform.ui.destinations.login.LoginViewModel
import com.commonground.client.multiplatform.ui.destinations.onboarding.Onboarding
import com.commonground.client.multiplatform.ui.destinations.onboarding.OnboardingNavActions
import com.commonground.client.multiplatform.ui.destinations.onboarding.OnboardingViewModel
import com.commonground.client.multiplatform.ui.destinations.signup.SignUp
import com.commonground.client.multiplatform.ui.destinations.signup.SignUpNavActions
import com.commonground.client.multiplatform.ui.destinations.signup.SignUpViewModel
import com.commonground.client.multiplatform.ui.destinations.user.User
import com.commonground.client.multiplatform.ui.destinations.user.UserNavActions
import com.commonground.client.multiplatform.ui.destinations.user.UserViewModel
import com.commonground.core.EventId
import com.commonground.core.UserId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUi(repoStore: RepoStore, startDestination: Route = Route.Login) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isAuthFlow = currentDestination?.let {
        it.hasRoute<Route.Login>() ||
                it.hasRoute<Route.SignUp>() ||
                it.hasRoute<Route.Onboarding>()
    } ?: false

    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        if (isAuthFlow) {
            NavGraph(navController, repoStore, startDestination)
        } else {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        navigationIcon = { NavIcon(navController, scope, drawerState) },
                        title = { Text("CommonGround") }
                    )
                },
                content = { padding ->
                    ModalNavigationDrawer(
                        modifier = Modifier.padding(padding).fillMaxSize(),
                        drawerState = drawerState,
                        drawerContent = { DrawerSheet { navController.navigate(it.toRoute()) } },
                        content = { NavGraph(navController, repoStore, startDestination) }
                    )
                }
            )
        }
    }
}

@Composable
private fun NavGraph( navController: NavHostController, repoStore: RepoStore, startDestination: Route
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable<Route.Login> {
            Login(
                viewModel = viewModel {
                    LoginViewModel(
                        onLoginSuccess = {
                            navController.navigate(Route.Home) {
                                popUpTo(Route.Login) { inclusive = true }
                            }
                        }
                    )
                },
                navActions = object : LoginNavActions {
                    override fun toSignUp() { navController.navigate(Route.SignUp) }
                }
            )
        }

        composable<Route.SignUp> {
            SignUp(
                viewModel = viewModel {
                    SignUpViewModel(
                        onSignUpSuccess = {
                            navController.navigate(Route.Onboarding) {
                                popUpTo(Route.Login) { inclusive = true }
                            }
                        }
                    )
                },
                navActions = object : SignUpNavActions {
                    override fun toLogin() { navController.popBackStack() }
                }
            )
        }

        composable<Route.Onboarding> {
            Onboarding(
                viewModel = viewModel {
                    OnboardingViewModel(
                        onFinished = {
                            navController.navigate(Route.Home) {
                                popUpTo(Route.Onboarding) { inclusive = true }
                            }
                        }
                    )
                },
                navActions = object : OnboardingNavActions {}
            )
        }

        composable<Route.Home> {
            Home(
                viewModel = viewModel { HomeViewModel(repoStore.eventRepo) },
                navActions = object : HomeNavActions {
                    override fun toEventDetails(id: EventId) { navController.navigate(Route.Event(id.value)) }
                    override fun toUser(id: UserId) { navController.navigate(Route.User(id.value)) }
                }
            )
        }
        composable<Route.Me> { Text("Me Screen") }
        composable<Route.Settings> { Text("Settings Screen") }
        composable<Route.Event> { entry ->
            val eventRoute = entry.toRoute<Route.Event>()
            EventDetails(
                viewModel = viewModel { EventDetailsViewModel(EventId(eventRoute.id)) },
                navActions = object : EventDetailsNavActions {
                    override fun toUser(id: UserId) { navController.navigate(Route.User(id.value)) }
                }
            )
        }
        composable<Route.User> { entry ->
            val route = entry.toRoute<Route.User>()
            User(
                viewModel = viewModel { UserViewModel(UserId(route.id)) },
                navActions = object : UserNavActions {
                    override fun toUser(id: UserId) { navController.navigate(Route.User(id.value)) }
                    override fun toEvent(id: EventId) { navController.navigate(Route.Event(id.value)) }
                }
            )
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
    Me,
    Home,
    Friends,
    Settings
}

private fun NavigationDrawerItem.toRoute(): Route {
    return when (this) {
        NavigationDrawerItem.Me -> Route.Me
        NavigationDrawerItem.Home -> Route.Home
        NavigationDrawerItem.Friends -> Route.Friends
        NavigationDrawerItem.Settings -> Route.Settings
    }
}