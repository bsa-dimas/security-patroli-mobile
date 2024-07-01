package com.bsalogistics.securitypatroli.screen.unauth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.bsalogistics.securitypatroli.component.MediumTitleText
import com.bsalogistics.securitypatroli.component.TitleText
import com.bsalogistics.securitypatroli.screen.unauth.login.state.LoginUiEvent
import com.bsalogistics.securitypatroli.ui.theme.AppTheme
import com.bsalogistics.securitypatroli.R
import com.bsalogistics.securitypatroli.component.AlertDialogModel
import com.bsalogistics.securitypatroli.component.AlertDialogType
import com.bsalogistics.securitypatroli.component.AlertDialogWithIcon
import com.bsalogistics.securitypatroli.component.CompanyLogo
import com.bsalogistics.securitypatroli.component.ListError
import com.bsalogistics.securitypatroli.component.LoadingDialog
import com.bsalogistics.securitypatroli.component.MyAlertDialog
import com.bsalogistics.securitypatroli.models.User
import com.bsalogistics.securitypatroli.network.APIResponse
import com.bsalogistics.securitypatroli.network.BaseResponse
import com.bsalogistics.securitypatroli.utils.PreferencesManager
import com.google.gson.Gson
import timber.log.Timber


@Composable
fun LoginFormScreen(
    loginFormViewModel: LoginFormViewModel = hiltViewModel(),
    onNavigateHome : () -> Unit = {}
) {

    val context = LocalContext.current

    val openDialog = rememberSaveable {
        mutableStateOf(false)
    }

    val loginState by loginFormViewModel.login.collectAsState(null)

    when (loginState) {
        is APIResponse.Error -> {
            (loginState as (APIResponse.Error)).errorMsg?.let {
                openDialog.value = true
                MyAlertDialog(AlertDialogModel(showDialog = openDialog.value, msg = it, typeDialog = AlertDialogType.FAILURE), onConfirm = {
                    loginFormViewModel.resetState()
                    openDialog.value.not()
                } )
            }
        }
        is APIResponse.Loading -> {
            LoadingDialog("Mohon tunggu")
        }
        is APIResponse.Success -> {
            (loginState as (APIResponse.Success)).data?.data?.let {
                PreferencesManager(context).saveDataUser(it)
                onNavigateHome.invoke()
            }
        }
        null -> {}
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Spacer(modifier = Modifier.weight(1f))

        Profile()
        
        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)) {
            LoginFormInput(viewModel = loginFormViewModel)
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(modifier = Modifier.safeDrawingPadding()) {
            CompanyLogo()
        }
    }
}

@Composable
private fun Profile() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Image(painterResource(id = R.drawable.policeman), contentDescription = "icon", modifier = Modifier.height(100.dp))
            Text(text = "Security Patroli", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    onNavigateToRegistration: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToAuthenticatedRoute: () -> Unit
) {

    val loginState by remember {
        loginViewModel.loginState
    }

    if (loginState.isLoginSuccessful) {
        /**
         * Navigate to Authenticated navigation route
         * once login is successful
         */
        LaunchedEffect(key1 = true) {
            onNavigateToAuthenticatedRoute.invoke()
        }
    } else {

        // Full Screen Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Main card Content for Login
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimens.paddingLarge)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = AppTheme.dimens.paddingLarge)
                        .padding(bottom = AppTheme.dimens.paddingExtraLarge)
                ) {

                    // Heading Jetpack Compose
                    MediumTitleText(
                        modifier = Modifier
                            .padding(top = AppTheme.dimens.paddingLarge)
                            .fillMaxWidth(),
                        text = stringResource(id = R.string.jetpack_compose),
                        textAlign = TextAlign.Center
                    )

                    // Login Logo
                    AsyncImage(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                            .padding(top = AppTheme.dimens.paddingSmall),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(data = R.drawable.ic_launcher_foreground)
                            .crossfade(enable = true)
                            .scale(Scale.FILL)
                            .build(),
                        contentDescription = stringResource(id = R.string.login_heading_text)
                    )

                    // Heading Login
                    TitleText(
                        modifier = Modifier.padding(top = AppTheme.dimens.paddingLarge),
                        text = stringResource(id = R.string.login_heading_text)
                    )

                    // Login Inputs Composable
                    LoginInputs(
                        loginState = loginState,
                        onEmailOrMobileChange = { inputString ->
                            loginViewModel.onUiEvent(
                                loginUiEvent = LoginUiEvent.EmailOrMobileChanged(
                                    inputString
                                )
                            )
                        },
                        onPasswordChange = { inputString ->
                            loginViewModel.onUiEvent(
                                loginUiEvent = LoginUiEvent.PasswordChanged(
                                    inputString
                                )
                            )
                        },
                        onSubmit = {
                            loginViewModel.onUiEvent(loginUiEvent = LoginUiEvent.Submit)
                        },
                        onForgotPasswordClick = onNavigateToForgotPassword
                    )

                }
            }

            // Register Section
            Row(
                modifier = Modifier.padding(AppTheme.dimens.paddingNormal),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Don't have an account?
                Text(text = stringResource(id = R.string.do_not_have_account))

                //Register
                Text(
                    modifier = Modifier
                        .padding(start = AppTheme.dimens.paddingExtraSmall)
                        .clickable {
                            onNavigateToRegistration.invoke()
                        },
                    text = stringResource(id = R.string.register),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

    }

}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    MaterialTheme {
        LoginScreen(
            onNavigateToForgotPassword = {},
            onNavigateToRegistration = {},
            onNavigateToAuthenticatedRoute = {}
        )
    }
}