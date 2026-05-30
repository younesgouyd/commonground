package com.commonground.client.multiplatform.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.commonground.client.multiplatform.data.PlatformFileStorage
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUi(fileStorage: PlatformFileStorage) {
    val navController = rememberNavController()
    val viewModel = viewModel {
        MainUiViewModel(
            fileStorage = fileStorage,
            onLogout = { navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } } }
        )
    }
    val repoStore = viewModel.repoStore

    val startDestination by viewModel.startDestination
    if (startDestination == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val isAuthFlow = currentDestination?.let {
        it.hasRoute<Route.Login>() ||
        it.hasRoute<Route.SignUp>() ||
        it.hasRoute<Route.Onboarding>()
    } ?: false
    val inHome = currentDestination?.hasRoute<Route.Home>() == true

    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        if (isAuthFlow) {
            NavGraph(navController, repoStore, startDestination!!)
        } else {
            Scaffold(
                topBar = {
                    if (inHome) {
                        HomeTopBar(navController, viewModel::logout)
                    } else {
                        CenterAlignedTopAppBar(
                            modifier = Modifier.fillMaxWidth(),
                            navigationIcon = {
                                IconButton(onClick = { navController.popBackStack() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                }
                            },
                            title = { Text("CommonGround") }
                        )
                    }
                },
                content = { padding ->
                    Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                        NavGraph(navController, repoStore, startDestination!!)
                    }
                }
            )
        }
    }
}

@Composable
private fun NavGraph( navController: NavHostController, repoStore: RepoStore, startDestination: Route) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable<Route.Login> {
            Login(
                viewModel = viewModel {
                    LoginViewModel(
                        authRepo = repoStore.authRepo,
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
                        authRepo = repoStore.authRepo,
                        onSignUpSuccess = {
                            navController.navigate(Route.Onboarding) {
                                popUpTo(Route.SignUp) { inclusive = true }
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
                    override fun toEventDetails(id: String) { navController.navigate(Route.Event(id)) }
                    override fun toUser(id: String) { navController.navigate(Route.User(id)) }
                }
            )
        }
        composable<Route.Me> { Text("Me Screen") }
        composable<Route.Settings> { Text("Settings Screen") }
        composable<Route.Event> { entry ->
            val eventRoute = entry.toRoute<Route.Event>()
            EventDetails(
                viewModel = viewModel { EventDetailsViewModel(eventRoute.id) },
                navActions = object : EventDetailsNavActions {
                    override fun toUser(id: String) { navController.navigate(Route.User(id)) }
                }
            )
        }
        composable<Route.User> { entry ->
            val route = entry.toRoute<Route.User>()
            User(
                viewModel = viewModel { UserViewModel(route.id) },
                navActions = object : UserNavActions {
                    override fun toUser(id: String) { navController.navigate(Route.User(id)) }
                    override fun toEvent(id: String) { navController.navigate(Route.Event(id)) }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(
    navController: NavHostController,
    onLogoutClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        modifier = Modifier.fillMaxWidth(),
        navigationIcon = {
            Icon(
                Icons.Default.Groups,
                contentDescription = "CommonGround",
                modifier = Modifier.padding(start = 8.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = { Text("CommonGround") },
        actions = {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Profile") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, null) },
                        onClick = {
                            menuExpanded = false
                            navController.navigate(Route.Me)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        leadingIcon = { Icon(Icons.Default.Settings, null) },
                        onClick = {
                            menuExpanded = false
                            navController.navigate(Route.Settings)
                        }
                    )
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, null) },
                        onClick = {
                            menuExpanded = false
                            onLogoutClick()
                        }
                    )
                }
            }
        }
    )
}