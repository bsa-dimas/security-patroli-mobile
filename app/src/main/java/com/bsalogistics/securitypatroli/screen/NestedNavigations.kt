package com.bsalogistics.securitypatroli.screen

import android.os.Build
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.bsalogistics.securitypatroli.component.AlertDialogModel
import com.bsalogistics.securitypatroli.component.AlertDialogType
import com.bsalogistics.securitypatroli.component.LoadingDialog
import com.bsalogistics.securitypatroli.component.MyAlertDialog
import com.bsalogistics.securitypatroli.network.Area
import com.bsalogistics.securitypatroli.screen.areaadmin.AddAreaFormScreen
import com.bsalogistics.securitypatroli.screen.areaadmin.AreaAdminScreen
import com.bsalogistics.securitypatroli.screen.areaadmin.ReportAreaScreen
import com.bsalogistics.securitypatroli.screen.areaadmin.ScannerAddAreaScreen
import com.bsalogistics.securitypatroli.screen.areasecurity.AreaListSecurity
import com.bsalogistics.securitypatroli.screen.areasecurity.form.AreaFormDetailScreen
import com.bsalogistics.securitypatroli.screen.areasecurity.form.AreaFormScreen
import com.bsalogistics.securitypatroli.screen.areasecurity.form.ScannerSecurityScreen
import com.bsalogistics.securitypatroli.screen.auth.dashboard.DashboardScreen
import com.bsalogistics.securitypatroli.screen.unauth.login.LoginFormScreen
import com.bsalogistics.securitypatroli.utils.PreferencesManager
import com.bsalogistics.securitypatroli.utils.getCurrentLocationScanner
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import timber.log.Timber

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

fun NavGraphBuilder.areaGraph(navController: NavController) {

    navigation(
        route = NavigationRoutes.Area.NavigationRoute.route,
        startDestination = NavigationRoutes.Area.AreaListForSecurity.route
    ) {

        composable(route = NavigationRoutes.Area.AreaAdmin.route) {
            DashboardScreen(navController = navController)
        }

        composable(route = NavigationRoutes.Area.AreaListForAdmin.route) {
            AreaAdminScreen(navController = navController)
        }

        composable(route = NavigationRoutes.Area.ReportAreaScreen.route) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ReportAreaScreen(navController = navController)
            }
        }

        composable(route = NavigationRoutes.Area.AddAreaForm.route) { backStackEntry ->

            val area = backStackEntry.savedStateHandle.get<String>("areaGson")
            AddAreaFormScreen(navController = navController, areaGson = area)
        }

        composable(route = NavigationRoutes.Area.ScannerAddAreaScreen.route) {

            val context = LocalContext.current
            val fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            val openDialog = rememberSaveable {
                mutableStateOf(false)
            }
            val errorOpenDialog = rememberSaveable {
                mutableStateOf(false)
            }
            val areaNameCallback = rememberSaveable {
                mutableStateOf<String?>(null)
            }

            if (openDialog.value) {
                LoadingDialog("Mencari lokasi")
            }

            if (errorOpenDialog.value) {
                MyAlertDialog(AlertDialogModel(showDialog = errorOpenDialog.value, msg = "Error Get Location", typeDialog = AlertDialogType.FAILURE), onConfirm = {
                    errorOpenDialog.value = false
                    navController.navigateUp()
                } )
            }

            areaNameCallback.value?.let { areaName ->
                LaunchedEffect(key1 = Unit) {
                    openDialog.value = true

                    getCurrentLocationScanner(
                        onGetCurrentLocationSuccess = {
                            openDialog.value = false

                            val areaGson = Gson().toJson(Area(name = areaName, latitude = it.first, longitude = it.second))

                            navController.popBackStack()
                            navController.currentBackStackEntry?.savedStateHandle?.set("areaGson", areaGson)

                            Timber
                                .tag("MYTAG")
                                .e("onGetCurrentLocationSuccess LATITUDE: ${it.first}, LONGITUDE: ${it.second})}")
                        },
                        onGetCurrentLocationFailed = {
                            errorOpenDialog.value = true
                            openDialog.value = false

                            Timber
                                .tag("MYTAG")
                                .e("onGetCurrentLocationFailed ${it.localizedMessage}")
                        }, fusedLocationProviderClient = fusedLocationProviderClient, context = context
                    )
                }
            }

            ScannerAddAreaScreen(navController = navController, onSuccess = { areaName ->

                areaNameCallback.value = areaName

            })
        }


        /**
         * FOR SECURITY
         */

        composable(route = "${NavigationRoutes.Area.AreaListForSecurity.route}/{typeuser}",
            arguments = listOf(navArgument("typeuser") { type = NavType.StringType })) { backStackEntry ->

            AreaListSecurity(navController = navController, typeuser = backStackEntry.arguments?.getString("typeuser") ?: "")
        }

        composable(route = NavigationRoutes.Area.AreaListForSecurity.route) {
            AreaListSecurity(navController = navController, typeuser = "")
        }

        composable(
            route = "${NavigationRoutes.Area.AreaFormScreen.route}/{areaId}",
            arguments = listOf(navArgument("areaId") { type = NavType.StringType })) { backStackEntry ->

            AreaFormScreen(navController = navController, areaId = backStackEntry.arguments?.getString("areaId") ?: "")
        }

        composable(
            route = "${NavigationRoutes.Area.AreaFormDetailScreen.route}/{areaId}",
            arguments = listOf(navArgument("areaId") { type = NavType.StringType })) { backStackEntry ->

            AreaFormDetailScreen(navController = navController, areaId = backStackEntry.arguments?.getString("areaId") ?: "Null")
        }

        composable(route = NavigationRoutes.Area.ScannerSecurityScreen.route) {

            ScannerSecurityScreen(navController = navController, onSuccess = { areaId ->

//                navController.navigate("${NavigationRoutes.Area.AreaFormScreen.route}/$areaId")

                navController.navigate("${NavigationRoutes.Area.AreaFormScreen.route}/$areaId") {
                    popUpTo(route = NavigationRoutes.Area.ScannerSecurityScreen.route) {
                        inclusive = true
                    }
                }

            }, onFailed = {

            })
        }
    }
}