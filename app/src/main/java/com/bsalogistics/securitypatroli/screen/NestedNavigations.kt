package com.bsalogistics.securitypatroli.screen

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaListSecurity
import com.bsalogistics.securitypatroli.screen.areasecurity.form.AreaFormScreen
import com.bsalogistics.securitypatroli.screen.areasecurity.form.ScannerSecurityScreen
import com.bsalogistics.securitypatroli.screen.auth.dashboard.DashboardScreen
import com.bsalogistics.securitypatroli.screen.unauth.login.LoginFormScreen
import com.bsalogistics.securitypatroli.utils.PreferencesManager

fun NavGraphBuilder.unauthenticatedGraph(navController: NavController) {

    navigation(
        route = NavigationRoutes.Unauthenticated.NavigationRoute.route,
        startDestination = NavigationRoutes.Unauthenticated.Login.route
    ) {

        // Login
        composable(route = NavigationRoutes.Unauthenticated.Login.route) {
            val context = LocalContext.current

            /**
             * Type User: Security
             */
            if (PreferencesManager(context).getDataUser().token.isNotBlank()) {
                val user = PreferencesManager(context).getDataUser()
                if (user.typeuser == "admin") {
                    navController.navigate(route = NavigationRoutes.Area.AreaAdmin.route) {
                        popUpTo(route = NavigationRoutes.Unauthenticated.NavigationRoute.route) {
                            inclusive = true
                        }
                    }
                } else {
                    navController.navigate(route = NavigationRoutes.Area.NavigationRoute.route) {
                        popUpTo(route = NavigationRoutes.Unauthenticated.NavigationRoute.route) {
                            inclusive = true
                        }
                    }
                }

            } else {
                /**
                 * Type User: Security
                 */
                LoginFormScreen(onNavigateHome = {

                    val user = PreferencesManager(context).getDataUser()
                    if (user.typeuser == "admin") {
                        navController.navigate(route = NavigationRoutes.Area.AreaAdmin.route)
                    } else {
                        navController.navigate(route = NavigationRoutes.Area.NavigationRoute.route)
                    }

                })
            }

        }

    }
}

/**
 * Authenticated screens nav graph builder
 */
//fun NavGraphBuilder.authenticatedGraph(navController: NavController) {
//    navigation(
//        route = NavigationRoutes.Authenticated.NavigationRoute.route,
//        startDestination = NavigationRoutes.Authenticated.Dashboard.route
//    ) {
//        // Dashboard
//        composable(route = NavigationRoutes.Authenticated.Dashboard.route) {
//            HomeScreen(navController = navController)
//        }
//
////        composable(route = NavigationRoutes.Authenticated.AreaList.route) {
////            AreaScreen(navController = navController)
////        }
////
////        composable(route = NavigationRoutes.Authenticated.AreaScannedList.route) {navBackResult ->
////            val areaName = navBackResult.savedStateHandle.get<String>("areaName")
//////            DashboardForUser(navController = navController, areaName = areaName)
////
////
////            AreaFormNavigate(navController = navController)
////        }
////
////        composable(route = NavigationRoutes.Authenticated.AreaForm.route) { navBackResult ->
////
////            val areaName = navBackResult.savedStateHandle.get<String>("areaName")
//////            AreaFormScreen(navController = navController, areaName = areaName)
////        }
////
////        composable(route = "${NavigationRoutes.Authenticated.AreaFormScanned.route}/{areaName}") {navBackResult ->
////            AreaScannedFormScreen(navController = navController)
////        }
////
////        composable(route = NavigationRoutes.Authenticated.Scanner.route) {
////            ScannerScreen(navController = navController, onSuccess =  { areaName ->
////
////            })
////        }
//    }
//}

fun NavGraphBuilder.areaGraph(navController: NavController) {

    navigation(
        route = NavigationRoutes.Area.NavigationRoute.route,
        startDestination = NavigationRoutes.Area.AreaListForSecurity.route
    ) {

        composable(route = NavigationRoutes.Area.AreaAdmin.route) {
            DashboardScreen(navController = navController)
        }

        composable(route = "${NavigationRoutes.Area.AreaListForSecurity.route}/{typeuser}",
            arguments = listOf(navArgument("typeuser") { type = NavType.StringType })) { backStackEntry ->

            AreaListSecurity(navController = navController, typeuser = backStackEntry.arguments?.getString("typeuser") ?: "")
        }

        composable(route = NavigationRoutes.Area.AreaListForSecurity.route) {
            AreaListSecurity(navController = navController, typeuser = "")
        }

        composable(
            route = "${NavigationRoutes.Area.AreaFormScreen.route}/{areaName}",
            arguments = listOf(navArgument("areaName") { type = NavType.StringType })) { backStackEntry ->

            AreaFormScreen(navController = navController, areaName = backStackEntry.arguments?.getString("areaName") ?: "Null")
        }

        composable(route = NavigationRoutes.Area.ScannerSecurityScreen.route) {

            ScannerSecurityScreen(navController = navController, onSuccess = { areaName ->

                navController.navigate("${NavigationRoutes.Area.AreaFormScreen.route}/$areaName") {
                    popUpTo(route = NavigationRoutes.Area.ScannerSecurityScreen.route) {
                        inclusive = true
                    }
                }

            }, onFailed = {

            })
        }
    }
}