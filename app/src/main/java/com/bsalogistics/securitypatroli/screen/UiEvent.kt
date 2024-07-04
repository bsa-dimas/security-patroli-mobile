package com.bsalogistics.securitypatroli.screen

import com.bsalogistics.securitypatroli.network.Area
import com.bsalogistics.securitypatroli.network.AreaFormTransaction

sealed interface UiEvent {
    data class Navigate(val route: String): UiEvent
    object SignOut : UiEvent
    data class GotoAreaDetail(val area: AreaFormTransaction) : UiEvent
    object NavigateBack : UiEvent
}