package com.bsalogistics.securitypatroli.screen.areaadmin

sealed interface AreaListAdminEvent {
    data object Init : AreaListAdminEvent
    data object GetList : AreaListAdminEvent
    data object AddArea : AreaListAdminEvent
    data class DropArea(val id: String) : AreaListAdminEvent
    data object OnErrorArea : AreaListAdminEvent
    data object OnSuccessArea : AreaListAdminEvent

}

sealed interface UiAreaAdminEvent {
    data object Init : UiAreaAdminEvent
    data object OnSuccessAddArea : UiAreaAdminEvent
    data object OnErrorArea : UiAreaAdminEvent
}