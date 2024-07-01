package com.bsalogistics.securitypatroli.screen.areasecurity.form

sealed interface UiAreaFormEvent {
    data object Init : UiAreaFormEvent
    data object CheckingArea : UiAreaFormEvent
    data object OutOfArea : UiAreaFormEvent
    data object TryGetLocation : UiAreaFormEvent
}