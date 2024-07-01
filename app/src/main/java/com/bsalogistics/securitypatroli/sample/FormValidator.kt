package com.bsalogistics.securitypatroli.sample

object FormValidator {

    fun validateEmail(email: String): Boolean {
        return email.isNotEmpty() && isEmailValid(email)
    }

    fun validatePassWord(password: String): Boolean {
        return  password.isNotEmpty() && password.length >= 6
    }

    fun validateConfirmationPassWord(
        password: String,
        confirmationPassword: String
    ): Boolean {
        return confirmationPassword.isNotEmpty() && confirmationPassword == password
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")

        return emailRegex.matches(email)
    }
}

data class FormValidationResult(
    val emailStatus: Boolean = false,
    val passwordStatus: Boolean = false
)