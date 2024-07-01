package com.bsalogistics.securitypatroli.screen.unauth.login.state

import com.bsalogistics.securitypatroli.R
import com.bsalogistics.securitypatroli.component.state.ErrorState

val emailOrMobileEmptyErrorState = ErrorState(
    hasError = true,
    errorMessageStringResource = R.string.login_error_msg_empty_email_mobile
)

val passwordEmptyErrorState = ErrorState(
    hasError = true,
    errorMessageStringResource = R.string.login_error_msg_empty_password
)