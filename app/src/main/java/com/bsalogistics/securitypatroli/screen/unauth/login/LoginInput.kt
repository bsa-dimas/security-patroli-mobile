package com.bsalogistics.securitypatroli.screen.unauth.login

import android.widget.Space
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bsalogistics.securitypatroli.R
import com.bsalogistics.securitypatroli.component.ButtonType
import com.bsalogistics.securitypatroli.component.EmailTextField
import com.bsalogistics.securitypatroli.component.InputTextField
import com.bsalogistics.securitypatroli.component.MyButton
import com.bsalogistics.securitypatroli.component.NormalButton
import com.bsalogistics.securitypatroli.component.PasswordTextField
import com.bsalogistics.securitypatroli.sample.SubmitFormEvent
import com.bsalogistics.securitypatroli.screen.unauth.login.state.LoginState
import com.bsalogistics.securitypatroli.ui.theme.AppTheme
import timber.log.Timber


@Composable
fun LoginFormInput(viewModel: LoginFormViewModel) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {

        InputTextField (
            onValueChanged = { viewModel.onAction(LoginFormEvent.EmailChanged(it)) },
            label = "Email",
            isError = viewModel.errorState.value.emailStatus,
            error = "Please Enter Valid Email"
        )

        InputTextField(
            onValueChanged = {
                viewModel.onAction(
                    LoginFormEvent.PasswordChanged(
                        it
                    )
                )
            },
            label = "Password",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = viewModel.errorState.value.passwordStatus,
            error = "Please Enter Valid Password (>=6)"
        )

        MyButton(modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
            enabled = viewModel.validForm.value,
            text = "Masuk",
            buttonType = ButtonType.PRIMARY, onClick = {
            viewModel.onAction(LoginFormEvent.Submit)
        })


    }
}

@Preview(showBackground = true)
@Composable
fun LoginFormInputPreview() {
    LoginFormInput(viewModel = viewModel())
}


@Composable
fun LoginInputs(
    loginState: LoginState,
    onEmailOrMobileChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {

    // Login Inputs Section
    Column(modifier = Modifier.fillMaxWidth()) {

        // Email or Mobile Number
        EmailTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimens.paddingLarge),
            value = loginState.emailOrMobile,
            onValueChange = onEmailOrMobileChange,
            label = stringResource(id = R.string.login_email_id_or_phone_label),
            isError = loginState.errorState.emailOrMobileErrorState.hasError,
            errorText = stringResource(id = loginState.errorState.emailOrMobileErrorState.errorMessageStringResource)
        )


        // Password
        PasswordTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimens.paddingLarge),
            value = loginState.password,
            onValueChange = onPasswordChange,
            label = stringResource(id = R.string.login_password_label),
            isError = loginState.errorState.passwordErrorState.hasError,
            errorText = stringResource(id = loginState.errorState.passwordErrorState.errorMessageStringResource),
            imeAction = ImeAction.Done
        )

        // Forgot Password
        Text(
            modifier = Modifier
                .padding(top = AppTheme.dimens.paddingSmall)
                .align(alignment = Alignment.End)
                .clickable {
                    onForgotPasswordClick.invoke()
                },
            text = stringResource(id = R.string.forgot_password),
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium
        )

        // Login Submit Button
        NormalButton(
            modifier = Modifier.padding(top = AppTheme.dimens.paddingLarge),
            text = stringResource(id = R.string.login_button_text),
            onClick = onSubmit
        )

    }
}