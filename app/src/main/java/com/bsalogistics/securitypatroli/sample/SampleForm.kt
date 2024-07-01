package com.bsalogistics.securitypatroli.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bsalogistics.securitypatroli.component.ButtonType
import com.bsalogistics.securitypatroli.component.InputTextField
import com.bsalogistics.securitypatroli.component.MyButton
import com.bsalogistics.securitypatroli.ui.theme.SecurityPatroliTheme

@Composable
fun SampelForm(viewModel: SampleFormViewModel = hiltViewModel()) {

    var text by remember {
        mutableStateOf("")
    }

    SecurityPatroliTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(modifier = Modifier
                .padding(innerPadding)
                .padding(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

                InputTextField (
                    onValueChanged = { viewModel.onAction(SubmitFormEvent.EmailChanged(it)) },
                    label = "Email",
                    isError = viewModel.errorState.value.emailStatus,
                    error = "Please Enter Valid Email"
                )

                InputTextField(
                    onValueChanged = {
                        viewModel.onAction(
                            SubmitFormEvent.PasswordChanged(
                                it
                            )
                        )
                    },
                    label = "Password",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = viewModel.errorState.value.passwordStatus,
                    error = "Please Enter Valid Password (>=6)"
                )

                MyButton(text = "Simpan", buttonType = ButtonType.PRIMARY, onClick = {
                    viewModel.onAction(SubmitFormEvent.Submit)
                })
//                MyButton(text = "Batal", buttonType = ButtonType.SECONDARY)

            }
        }
    }

}