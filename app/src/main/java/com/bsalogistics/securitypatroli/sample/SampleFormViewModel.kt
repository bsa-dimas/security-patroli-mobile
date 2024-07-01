package com.bsalogistics.securitypatroli.sample

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SampleFormViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

    private var _sampleState = mutableStateOf(SampleModel())
    val sampleState: State<SampleModel> = _sampleState

    private var _errorState = mutableStateOf(FormValidationResult())
    val errorState: State<FormValidationResult> = _errorState
    val validationEvent = MutableSharedFlow<ValidationState>()

    fun onAction(formEvent: SubmitFormEvent) {
        when (formEvent) {
            is SubmitFormEvent.EmailChanged -> {
                _sampleState.value = _sampleState.value.copy(
                    email = formEvent.email
                )
                validateSample()
            }
            is SubmitFormEvent.PasswordChanged -> {
                _sampleState.value = _sampleState.value.copy(
                    password = formEvent.passWord
                )
                validateSample()
            }
            else -> {
                validateSample()
            }
        }
    }


    var hasErrorWhenAdmin : Boolean = false
    var hasErrorWhenNotAdmin:Boolean = false
    fun validateSample() {
        val isEmailValid = FormValidator.validateEmail(_sampleState.value.email)
        val isPassWordValid = FormValidator.validatePassWord(_sampleState.value.password)
        _errorState.value = _errorState.value.copy(
            emailStatus = !isEmailValid
        )
        _errorState.value = _errorState.value.copy(
            passwordStatus = !isPassWordValid
        )

        hasErrorWhenNotAdmin = !( _errorState.value.emailStatus) &&
                !( _errorState.value.passwordStatus)

        viewModelScope.launch {
            if(hasErrorWhenNotAdmin){((
                    validationEvent.emit(ValidationState.Success(_sampleState.value))))
            }
        }
    }

}

sealed class SubmitFormEvent  {
    data class EmailChanged(val email: String): SubmitFormEvent()
    data class PasswordChanged(val passWord: String): SubmitFormEvent()
    object Submit: SubmitFormEvent()
}