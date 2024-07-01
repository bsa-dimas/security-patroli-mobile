@file:OptIn(ExperimentalMaterial3Api::class)

package com.bsalogistics.securitypatroli.component

import android.text.TextUtils
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsalogistics.securitypatroli.ui.theme.SecurityPatroliTheme

@Composable
fun InputTextField(
    modifier: Modifier = Modifier,
    input: String = "",
    onValueChanged: (String) -> Unit,
    label: String,
    error: String = "",
    responseError:String = "",
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    var supportingText: @Composable (() -> Unit)? = null

    var text by remember {
        mutableStateOf("")
    }

    if (isError) {
        supportingText = {
            if(!TextUtils.isEmpty(error)) {
                ShowErrorText(error)
            }
        }
    }

    OutlinedTextField(
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.LightGray
        ),
        isError = isError,
        value = text,
        onValueChange = {
            text = it
            onValueChanged(it)
        },
        label = { Text(label) },
        supportingText = supportingText,
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxWidth()

    )

//    OutlinedTextField(value = , onValueChange = )
}


@Composable
fun ShowErrorText(text:String){
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth()

    )
}

@Composable
@Preview(showBackground = true)
private fun MyTextFieldPreview() {
    SecurityPatroliTheme {
        InputTextField(label = "Text Field", onValueChanged = {})
    }
}