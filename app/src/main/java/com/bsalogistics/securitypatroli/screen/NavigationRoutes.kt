package com.bsalogistics.securitypatroli.screen

sealed class NavigationRoutes {

    // Unauthenticated Routes
    sealed class Unauthenticated(val route: String) : NavigationRoutes() {
        object Login : Unauthenticated(route = "login")
        object NavigationRoute : Unauthenticated(route = "unauthenticated")
        object Registration : Unauthenticated(route = "registration")
    }

    sealed class Area(val route: String) : NavigationRoutes() {
        object NavigationRoute : Area(route = "Area")
        object AreaAdmin : Area(route = "AreaAdmin")
        object AreaListForSecurity : Area(route = "AreaListForSecurity")
        object ScannerSecurityScreen : Area(route = "ScannerSecurityScreen")
        object AreaFormScreen : Area(route = "AreaFormScreen")
    }

}