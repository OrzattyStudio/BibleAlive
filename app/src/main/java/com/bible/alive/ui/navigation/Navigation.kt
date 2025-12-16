package com.bible.alive.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bible.alive.R
import com.bible.alive.ui.home.HomeScreen
import com.bible.alive.ui.onboarding.OnboardingScreen
import com.bible.alive.ui.profile.ProfileScreen
import com.bible.alive.ui.read.BookSelectionScreen
import com.bible.alive.ui.read.ChapterReadScreen
import com.bible.alive.ui.read.ReadScreen
import com.bible.alive.ui.settings.SettingsScreen

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Main : Screen("main")
    data object Home : Screen("home")
    data object Read : Screen("read")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
    data object BookSelection : Screen("book_selection")
    data object ChapterRead : Screen("chapter/{bookId}/{chapter}") {
        fun createRoute(bookId: Int, chapter: Int) = "chapter/$bookId/$chapter"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val labelResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Home,
        labelResId = R.string.nav_home,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    ),
    BottomNavItem(
        screen = Screen.Read,
        labelResId = R.string.nav_read,
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book
    ),
    BottomNavItem(
        screen = Screen.Profile,
        labelResId = R.string.nav_profile,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    ),
    BottomNavItem(
        screen = Screen.Settings,
        labelResId = R.string.nav_settings,
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings
    )
)

@Composable
fun BibleAliveNavHost(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Main.route) {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = currentDestination?.route in listOf(
        Screen.Home.route,
        Screen.Read.route,
        Screen.Profile.route,
        Screen.Settings.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { 
                            it.route == item.screen.route 
                        } == true
                        
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = stringResource(item.labelResId)
                                )
                            },
                            label = { Text(stringResource(item.labelResId)) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToRead = { bookId, chapter ->
                        navController.navigate(Screen.ChapterRead.createRoute(bookId, chapter))
                    }
                )
            }
            
            composable(Screen.Read.route) {
                ReadScreen(
                    onNavigateToBook = { bookId ->
                        navController.navigate("book_selection/$bookId")
                    },
                    onNavigateToChapter = { bookId, chapter ->
                        navController.navigate(Screen.ChapterRead.createRoute(bookId, chapter))
                    }
                )
            }
            
            composable(
                route = "book_selection/{bookId}",
                arguments = listOf(navArgument("bookId") { type = NavType.IntType })
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getInt("bookId") ?: 1
                BookSelectionScreen(
                    bookId = bookId,
                    onChapterSelected = { chapter ->
                        navController.navigate(Screen.ChapterRead.createRoute(bookId, chapter))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Screen.ChapterRead.route,
                arguments = listOf(
                    navArgument("bookId") { type = NavType.IntType },
                    navArgument("chapter") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getInt("bookId") ?: 1
                val chapter = backStackEntry.arguments?.getInt("chapter") ?: 1
                ChapterReadScreen(
                    bookId = bookId,
                    chapter = chapter,
                    onBack = { navController.popBackStack() },
                    onNextChapter = { nextBook, nextChapter ->
                        navController.navigate(Screen.ChapterRead.createRoute(nextBook, nextChapter)) {
                            popUpTo(Screen.ChapterRead.route) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
