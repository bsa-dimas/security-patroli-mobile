package com.bsalogistics.securitypatroli.screen.areasecurity.form

sealed interface UiAreaFormEvent {
    data object Init : UiAreaFormEvent
    data object OutOfArea : UiAreaFormEvent
    data object TryGetLocation : UiAreaFormEvent
    data object SuccessGetLocation : UiAreaFormEvent
    data object FailedGetLocation : UiAreaFormEvent
    data object LoadingGetLocation : UiAreaFormEvent
    data object RequireFormData : UiAreaFormEvent
}