package com.bsalogistics.securitypatroli.screen.unauth.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bsalogistics.securitypatroli.component.state.ErrorState
import com.bsalogistics.securitypatroli.models.User
import com.bsalogistics.securitypatroli.network.APIResponse
import com.bsalogistics.securitypatroli.network.APIStatus
import com.bsalogistics.securitypatroli.network.ApiInterfaceImpl
import com.bsalogistics.securitypatroli.network.BaseResponse
import com.bsalogistics.securitypatroli.network.LoginBody
import com.bsalogistics.securitypatroli.network.NetworkService
import com.bsalogistics.securitypatroli.sample.FormValidator
import com.bsalogistics.securitypatroli.sample.SampleModel
import com.bsalogistics.securitypatroli.screen.unauth.login.state.LoginErrorState
import com.bsalogistics.securitypatroli.screen.unauth.login.state.LoginState
import com.bsalogistics.securitypatroli.screen.unauth.login.state.LoginUiEvent
import com.bsalogistics.securitypatroli.screen.unauth.login.state.emailOrMobileEmptyErrorState
import com.bsalogistics.securitypatroli.screen.unauth.login.state.passwordEmptyErrorState
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject



@HiltViewModel
class LoginFormViewModel @Inject constructor(private val apiService: NetworkService) : ViewModel() {

    val apiInterfaceImpl = ApiInterfaceImpl(apiInterface = apiService)

    var validForm  = mutableStateOf(false)

    private var _loginFormState = mutableStateOf(SampleModel())
    val sampleState: State<SampleModel> = _loginFormState

    private var _errorState = mutableStateOf(LoginFormValidationResult())
    val errorState: State<LoginFormValidationResult> = _errorState

    private val _login = MutableStateFlow<APIResponse<BaseResponse<User>>>(APIResponse.Success(null))
    val login: StateFlow<APIResponse<BaseResponse<User>>> = _login

    fun resetState() {
        _login.value = APIResponse.Success(null)
    }

    fun login() {
        viewModelScope.launch {
            try {
                apiInterfaceImpl.login(LoginBody(
                    email = _loginFormState.value.email,
                    password = _loginFormState.value.password
                ))
                    .collect { res ->

                        when (res.status) {
                            APIStatus.LOADING -> {

                            }
                            APIStatus.SUCCESS -> {
                                res.data?.let {
                                    if (it.success) {
                                        _login.value = APIResponse.Success(it)
                                    } else {
                                        _login.value = APIResponse.Error(errorMsg = res.data.message )
                                    }
                                }
                            }
                            APIStatus.ERROR -> {
                                _login.value = APIResponse.Error(errorMsg = res.errorMsg )
                            }
                        }

                        Timber.tag("MYTAG").e("login...${Gson().toJson(res)}")

                    }
            } catch (ex: Exception) {
                _login.value = APIResponse.Error(errorMsg = ex.localizedMessage)
            }
        }
    }


    fun onAction(formEvent: LoginFormEvent) {
        when (formEvent) {
            is LoginFormEvent.EmailChanged -> {
                _loginFormState.value = _loginFormState.value.copy(
                    email = formEvent.email
                )

                val isEmailValid = FormValidator.validateEmail(_loginFormState.value.email)
                _errorState.value = _errorState.value.copy(
                    emailStatus = !isEmailValid
                )

                validForm.value = !( _errorState.value.emailStatus) &&
                        !( _errorState.value.passwordStatus)

            }
            is LoginFormEvent.PasswordChanged -> {
                _loginFormState.value = _loginFormState.value.copy(
                    password = formEvent.password
                )

                val isPassWordValid = FormValidator.validatePassWord(_loginFormState.value.password)

                _errorState.value = _errorState.value.copy(
                    passwordStatus = !isPassWordValid
                )

                validForm.value = !( _errorState.value.emailStatus) &&
                        !( _errorState.value.passwordStatus)

            }
            is LoginFormEvent.Submit -> {
                validationForm()
            }
        }
    }


    private fun validationForm() {

        viewModelScope.launch {
            if(validForm.value){
                _login.value = APIResponse.Loading()
                login()
            }
        }
    }

}


data class LoginFormValidationResult (
    val emailStatus: Boolean = false,
    val passwordStatus: Boolean = false
)

sealed class LoginFormEvent  {
    data class EmailChanged(val email: String): LoginFormEvent()
    data class PasswordChanged(val password: String): LoginFormEvent()
    object Submit: LoginFormEvent()
}

/**
 * ViewModel for Login Screen
 */
@HiltViewModel
class LoginViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

    var loginState = mutableStateOf(LoginState())
        private set

    /**
     * Function called on any login event [LoginUiEvent]
     */
    fun onUiEvent(loginUiEvent: LoginUiEvent) {
        when (loginUiEvent) {

            // Email/Mobile changed
            is LoginUiEvent.EmailOrMobileChanged -> {
                loginState.value = loginState.value.copy(
                    emailOrMobile = loginUiEvent.inputValue,
                    errorState = loginState.value.errorState.copy(
                        emailOrMobileErrorState = if (loginUiEvent.inputValue.trim().isNotEmpty())
                            ErrorState()
                        else
                            emailOrMobileEmptyErrorState
                    )
                )
            }

            // Password changed
            is LoginUiEvent.PasswordChanged -> {
                loginState.value = loginState.value.copy(
                    password = loginUiEvent.inputValue,
                    errorState = loginState.value.errorState.copy(
                        passwordErrorState = if (loginUiEvent.inputValue.trim().isNotEmpty())
                            ErrorState()
                        else
                            passwordEmptyErrorState
                    )
                )
            }

            // Submit Login
            is LoginUiEvent.Submit -> {
                val inputsValidated = validateInputs()
                if (inputsValidated) {
                    // TODO Trigger login in authentication flow
                    loginState.value = loginState.value.copy(isLoginSuccessful = true)
                }
            }
        }
    }

    /**
     * Function to validate inputs
     * Ideally it should be on domain layer (usecase)
     * @return true -> inputs are valid
     * @return false -> inputs are invalid
     */
    private fun validateInputs(): Boolean {
        val emailOrMobileString = loginState.value.emailOrMobile.trim()
        val passwordString = loginState.value.password
        return when {

            // Email/Mobile empty
            emailOrMobileString.isEmpty() -> {
                loginState.value = loginState.value.copy(
                    errorState = LoginErrorState(
                        emailOrMobileErrorState = emailOrMobileEmptyErrorState
                    )
                )
                false
            }

            //Password Empty
            passwordString.isEmpty() -> {
                loginState.value = loginState.value.copy(
                    errorState = LoginErrorState(
                        passwordErrorState = passwordEmptyErrorState
                    )
                )
                false
            }

            // No errors
            else -> {
                // Set default error state
                loginState.value = loginState.value.copy(errorState = LoginErrorState())
                true
            }
        }
    }

}