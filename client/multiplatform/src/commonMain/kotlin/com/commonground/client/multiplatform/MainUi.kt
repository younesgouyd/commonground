package com.commonground.client.multiplatform

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.commonground.client.multiplatform.destinations.home.Home
import com.commonground.client.multiplatform.destinations.home.HomeNavActions

@Composable
fun MainUi() {
    val navController = rememberNavController()
    var selectedNavigationDrawerItem by remember { mutableStateOf(NavigationDrawerItem.Home) }
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
        Surface(color = MaterialTheme.colorScheme.background) {
            ModalNavigationDrawer(
                modifier = Modifier.fillMaxSize(),
                drawerState = drawerState,
                drawerContent = {
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
                                    onClick = { navController.navigate(it.toRoute()) }
                                )
                            }
                        }
                    }
                }
            ) {
                NavHost(navController = navController, startDestination = Route.Home) {
                    composable<Route.Profile> { Text("Profile Screen") }
                    composable<Route.Home> {
                        Home(
                            navActions = object : HomeNavActions {
                                override fun toEventDetails(id: Long) { navController.navigate(Route.Event(id)) }
                            }
                        )
                    }
                    composable<Route.Friends> { Text("Friends Screen") }
                    composable<Route.Settings> { Text("Settings Screen") }
                    composable<Route.Event> { Text("Event Screen") }
                }
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