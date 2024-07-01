package com.bsalogistics.securitypatroli.screen.unauth.login.state

/**
 * Login Screen Events
 */
sealed class LoginUiEvent {
    data class EmailOrMobileChanged(val inputValue: String) : LoginUiEvent()
    data class PasswordChanged(val inputValue: String) : LoginUiEvent()
    object Submit : LoginUiEvent()
}