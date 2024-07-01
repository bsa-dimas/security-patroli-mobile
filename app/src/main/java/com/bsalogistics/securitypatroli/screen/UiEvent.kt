package com.bsalogistics.securitypatroli.screen

sealed interface UiEvent {
    data class Navigate(val route: String): UiEvent
    object SignOut : UiEvent
    object NavigateBack : UiEvent
}