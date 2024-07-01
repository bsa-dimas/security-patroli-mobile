package com.bsalogistics.securitypatroli.screen

sealed class NavigationRoutes {

    // Unauthenticated Routes
    sealed class Unauthenticated(val route: String) : NavigationRoutes() {
        object Login : Unauthenticated(route = "login")
        object NavigationRoute : Unauthenticated(route = "unauthenticated")
        object Registration : Unauthenticated(route = "registration")
    }

    // Authenticated Routes
    sealed class Authenticated(val route: String) : NavigationRoutes() {
        object NavigationRoute : Authenticated(route = "authenticated")
        object Dashboard : Authenticated(route = "Dashboard")
        object AreaList : Authenticated(route = "AreaList")
        object AreaScannedList : Authenticated(route = "AreaScannedList")
        object AreaForm : Authenticated(route = "AreaForm")
        object AreaFormScanned : Authenticated(route = "AreaFormScanned")
        object Scanner : Authenticated(route = "Scanner")
    }

    sealed class Area(val route: String) : NavigationRoutes() {
        object NavigationRoute : Area(route = "Area")
        object AreaAdmin : Area(route = "AreaAdmin")
        object AreaListForSecurity : Area(route = "AreaListForSecurity")
        object ScannerSecurityScreen : Area(route = "ScannerSecurityScreen")
        object AreaFormScreen : Area(route = "AreaFormScreen")
    }

}