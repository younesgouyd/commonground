package com.commonground.client.multiplatform.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.commonground.client.multiplatform.data.PlatformFileStorage
import com.commonground.client.multiplatform.data.RepoStore
import com.commonground.client.multiplatform.ui.destinations.createevent.CreateEvent
import com.commonground.client.multiplatform.ui.destinations.createevent.CreateEventNavActions
import com.commonground.client.multiplatform.ui.destinations.createevent.CreateEventViewModel
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
import com.commonground.client.multiplatform.ui.destinations.profile.Profile
import com.commonground.client.multiplatform.ui.destinations.profile.ProfileViewModel
import com.commonground.client.multiplatform.ui.destinations.settings.Settings
import com.commonground.client.multiplatform.ui.destinations.signup.SignUp
import com.commonground.client.multiplatform.ui.destinations.signup.SignUpNavActions
import com.commonground.client.multiplatform.ui.destinations.signup.SignUpViewModel
import com.commonground.client.multiplatform.ui.destinations.updateevent.UpdateEvent
import com.commonground.client.multiplatform.ui.destinations.updateevent.UpdateEventNavActions
import com.commonground.client.multiplatform.ui.destinations.updateevent.UpdateEventViewModel
import com.commonground.client.multiplatform.ui.destinations.user.User
import com.commonground.client.multiplatform.ui.destinations.user.UserViewModel
import com.commonground.client.multiplatform.ui.widgets.ProfileNavActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUi(
    modifier: Modifier = Modifier,
    fileStorage: PlatformFileStorage
) {
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
            modifier = modifier.fillMaxSize(),
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
            AdaptiveUi(
                wide = {
                    Wide(
                        modifier = modifier,
                        currentDestination = currentDestination,
                        navController = navController,
                        viewModel = viewModel,
                        inHome = inHome,
                        repoStore = repoStore,
                        startDestination = startDestination
                    )
                },
                compact = {
                    Compact(
                        modifier = modifier,
                        inHome = inHome,
                        navController = navController,
                        currentDestination = currentDestination,
                        repoStore = repoStore,
                        startDestination = startDestination
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Wide(
    modifier: Modifier = Modifier,
    currentDestination: NavDestination?,
    navController: NavHostController,
    viewModel: MainUiViewModel,
    inHome: Boolean,
    repoStore: RepoStore,
    startDestination: Route?
) {
    Row(modifier = modifier.fillMaxSize()) {
        AppSidebar(
            currentDestination = currentDestination,
            onNavigate = { route ->
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            },
            onLogout = viewModel::logout
        )
        VerticalDivider()
        Scaffold(
            topBar = {
                if (!inHome) {
                    CenterAlignedTopAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                            }
                        },
                        title = {}
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

@Composable
private fun Compact(
    modifier: Modifier = Modifier,
    inHome: Boolean,
    navController: NavHostController,
    currentDestination: NavDestination?,
    repoStore: RepoStore,
    startDestination: Route?
) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = inHome,
                    onClick = {
                        navController.navigate(Route.Home) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = currentDestination?.hasRoute<Route.Profile>() == true,
                    onClick = {
                        navController.navigate(Route.Profile) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
                NavigationBarItem(
                    selected = currentDestination?.hasRoute<Route.Settings>() == true,
                    onClick = {
                        navController.navigate(Route.Settings) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") }
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

@Composable
private fun NavGraph(navController: NavHostController, repoStore: RepoStore, startDestination: Route) {
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
        composable<Route.UpdateEvent> { entry ->
            val eventRoute = entry.toRoute<Route.Event>()
            UpdateEvent(
                viewModel = viewModel {
                    UpdateEventViewModel(
                        id = eventRoute.id,
                        eventRepo = repoStore.eventRepo,
                        onDone = { navController.popBackStack() }
                    )
                },
                navActions = object : UpdateEventNavActions {
                    override fun onBack() { navController.popBackStack() }
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
                    override fun toCreateEvent() { navController.navigate(Route.CreateEvent) }
                }
            )
        }
        composable<Route.CreateEvent> {
            CreateEvent(
                viewModel = viewModel {
                    CreateEventViewModel(
                        eventRepo = repoStore.eventRepo,
                        onEventCreated = { eventId ->
                            navController.navigate(Route.Event(eventId)) {
                                popUpTo(Route.CreateEvent) { inclusive = true }
                            }
                        }
                    )
                },
                navActions = object : CreateEventNavActions {
                    override fun onBack() { navController.popBackStack() }
                }
            )
        }
        composable<Route.Profile> {
            Profile(
                viewModel = viewModel {
                    ProfileViewModel(
                        userRepo = repoStore.userRepo,
                        eventRepo = repoStore.eventRepo
                    )
                },
                navActions = object : ProfileNavActions {
                    override fun toEvent(id: String) { navController.navigate(Route.Event(id)) }
                    override fun toUser(id: String) { navController.navigate(Route.User(id)) }
                    override fun toCreateEvent() { navController.navigate(Route.CreateEvent) }
                }
            )
        }
        composable<Route.Settings> {
            Settings(
                onLogout = { navController.navigate(Route.Login) { popUpTo(0) { inclusive = true } } }
            )
        }
        composable<Route.Event> { entry ->
            val eventRoute = entry.toRoute<Route.Event>()
            EventDetails(
                viewModel = viewModel {
                    EventDetailsViewModel(
                        id = eventRoute.id,
                        eventRepo = repoStore.eventRepo,
                        userRepo = repoStore.userRepo,
                        authRepo = repoStore.authRepo,
                        chatRepo = repoStore.chatRepo
                    )
                },
                navActions = object : EventDetailsNavActions {
                    override fun toUser(id: String) { navController.navigate(Route.User(id)) }
                    override fun toUpdateEvent() { navController.navigate(Route.UpdateEvent(eventRoute.id)) }
                    override fun onBack() { navController.popBackStack() }
                }
            )
        }
        composable<Route.User> { entry ->
            val route = entry.toRoute<Route.User>()
            User(
                viewModel = viewModel {
                    UserViewModel(route.id,
                        userRepo = repoStore.userRepo,
                        eventRepo = repoStore.eventRepo
                    )
                },
                navActions = object : ProfileNavActions {
                    override fun toUser(id: String) { navController.navigate(Route.User(id)) }
                    override fun toEvent(id: String) { navController.navigate(Route.Event(id)) }
                    override fun toCreateEvent() { navController.navigate(Route.CreateEvent) }
                }
            )
        }
    }
}

@Composable
private fun AppSidebar(
    currentDestination: NavDestination?,
    onNavigate: (Route) -> Unit,
    onLogout: () -> Unit
) {
    val inHome = currentDestination?.hasRoute<Route.Home>() == true
    val inProfile = currentDestination?.hasRoute<Route.Profile>() == true
    val inSettings = currentDestination?.hasRoute<Route.Settings>() == true

    Surface(
        modifier = Modifier.width(260.dp).fillMaxHeight(),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        "CommonGround",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Text(
                    "Navigation",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )

                SidebarNavItem(
                    icon = Icons.Default.Home,
                    label = "Home",
                    selected = inHome,
                    onClick = { onNavigate(Route.Home) }
                )
                SidebarNavItem(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    selected = inProfile,
                    onClick = { onNavigate(Route.Profile) }
                )
                SidebarNavItem(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    selected = inSettings,
                    onClick = { onNavigate(Route.Settings) }
                )
            }

            Column {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                SidebarNavItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = "Logout",
                    selected = false,
                    onClick = onLogout
                )
            }
        }
    }
}

@Composable
private fun SidebarNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(10.dp),
            color = containerColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = label,
                    modifier = Modifier.size(20.dp),
                    tint = contentColor
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}
