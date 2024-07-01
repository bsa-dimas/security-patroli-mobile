package com.bsalogistics.securitypatroli.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsalogistics.securitypatroli.ui.theme.SecurityPatroliTheme

enum class ButtonType {
    PRIMARY,
    SECONDARY
}

@Composable
fun MyButton(
    modifier: Modifier = Modifier,
    text: String = "Button",
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    buttonType: ButtonType = ButtonType.PRIMARY
) {

    when (buttonType) {
        ButtonType.PRIMARY -> {
            Button (
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
        else -> {

            Button (
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                colors = ButtonDefaults.outlinedButtonColors(),
                border = ButtonDefaults.outlinedButtonBorder,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(10.dp)
                )
            }

        }
    }


}

@Composable
@Preview(showBackground = true)
private fun MyButtonPreview() {
    SecurityPatroliTheme {
        MyButton(buttonType = ButtonType.PRIMARY)
    }
}
@Composable
@Preview(showBackground = true)
private fun MyButtonPreview2() {
    SecurityPatroliTheme {
        MyButton(buttonType = ButtonType.SECONDARY)
    }
}




