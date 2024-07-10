package com.bsalogistics.securitypatroli.screen

sealed class NavigationRoutes {

    // Unauthenticated Routes
    sealed class Unauthenticated(val route: String) : NavigationRoutes() {
        object Login : Unauthenticated(route = "login")
        object NavigationRoute : Unauthenticated(route = "unauthenticated")
        object Registration : Unauthenticated(route = "registration")
    }

//    sealed class AreaAdmin(val route: String) : NavigationRoutes() {
//        object NavigationRoute : AreaAdmin(route = "AreaAdmin")
//        object AreaAdmin : AreaAdmin(route = "AreaAdmin")
//    }

    sealed class Area(val route: String) : NavigationRoutes() {
        object NavigationRoute : Area(route = "Area")
        object AreaListForSecurity : Area(route = "AreaListForSecurity")
        object ScannerSecurityScreen : Area(route = "ScannerSecurityScreen")
        object AreaFormScreen : Area(route = "AreaFormScreen")
        object AreaFormDetailScreen : Area(route = "AreaFormDetailScreen")

        object AreaAdmin : Area(route = "AreaAdmin")
        object AreaListForAdmin : Area(route = "AreaListForAdmin")
        object AddAreaForm : Area(route = "AddAreaForm")
        object ScannerAddAreaScreen : Area(route = "ScannerAddAreaScreen")
        object ReportAreaScreen : Area(route = "ReportAreaScreen")
    }

}