package com.bsalogistics.securitypatroli.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bsalogistics.securitypatroli.R

enum class AlertDialogType {
    CONFIRM,
    OK,
    SUCCESS,
    FAILURE,
}

data class AlertDialogModel (
    val showDialog: Boolean = false,
    val msg: String = "msg",
    val typeDialog: AlertDialogType = AlertDialogType.OK
)

@Composable
fun ShowDialogSuccess(msg : String = "") {
    var open by rememberSaveable {
        mutableStateOf(true)
    }

    if (open) {
        MyAlertDialog(msg = msg)
    }
}

@Composable
fun MyAlertDialog(
    msg: String = "value",
    type: AlertDialogType = AlertDialogType.OK,
    onConfirm: () -> Unit = { },
    onDismiss: () -> Unit = { }
) {

    var open by rememberSaveable {
        mutableStateOf(true)
    }

    if (open) {
        if (type == AlertDialogType.SUCCESS) {
            AlertDialog(
                onDismissRequest = {
                },
                icon = {
                    Image(
                        painterResource(R.drawable.successicon),
                        contentDescription = "",
                        modifier = Modifier.width(50.dp)
                    )
                },
                title = {
                    Text(msg)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            open = false
                            onConfirm.invoke()
                        }
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        if (type == AlertDialogType.FAILURE) {
            AlertDialog(
                onDismissRequest = {
                },
                icon = {
                    Image(
                        painterResource(R.drawable.cancel),
                        contentDescription = "",
                        modifier = Modifier.width(50.dp)
                    )
                },
                title = {
                    Text(msg)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            open = false
                            onConfirm.invoke()
                        }
                    ) {
                        Text("OK")
                    }
                },
            )
        }

        if (type == AlertDialogType.CONFIRM) {
            AlertDialog(
                onDismissRequest = {
                },
                icon = {
                    Image(
                        painterResource(R.drawable.question),
                        contentDescription = "",
                        modifier = Modifier.width(50.dp)
                    )
                },
                title = {
                    Text(msg)
                },
                confirmButton = {
                    TextButton(
                        onClick = onConfirm
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Batal")
                    }

                }
            )
        }
    }
}

@Composable
fun MyAlertDialog (
    alertDialogModel: AlertDialogModel,
    onConfirm: () -> Unit = { },
    onDismiss: () -> Unit = { }
) {
    AlertDialogWithIcon(alertDialogModel.showDialog, alertDialogModel.msg, alertDialogModel.typeDialog, onConfirm, onDismiss)
}

@Preview
@Composable
fun AlertDialogWithIcon(
    openDialog: Boolean = false,
    desc: String = "value",
    type: AlertDialogType = AlertDialogType.OK,
    onConfirm: () -> Unit = { },
    onDismiss: () -> Unit = { },
) {

    if (openDialog) {

        if (type == AlertDialogType.SUCCESS) {
            AlertDialog(
                onDismissRequest = {
                },
                icon = {
                    Image(
                        painterResource(R.drawable.successicon),
                        contentDescription = "",
                        modifier = Modifier.width(50.dp)
                    )
                },
                title = {
                    Text(desc)
                },
                confirmButton = {
                    TextButton(
                        onClick = onConfirm
                    ) {
                        Text("OK")
                    }
                }
            )
        }

        if (type == AlertDialogType.FAILURE) {
            AlertDialog(
                onDismissRequest = onDismiss,
                icon = {
                    Image(
                        painterResource(R.drawable.cancel),
                        contentDescription = "",
                        modifier = Modifier.width(50.dp)
                    )
                },
                title = {
                    Text(desc)
                },
                confirmButton = {
                    TextButton(
                        onClick = onConfirm
                    ) {
                        Text("OK")
                    }
                },
            )
        }

        if (type == AlertDialogType.CONFIRM) {
            AlertDialog(
                onDismissRequest = onDismiss,
                icon = {
                    Image(
                        painterResource(R.drawable.question),
                        contentDescription = "",
                        modifier = Modifier.width(50.dp)
                    )
                },
                title = {
                    Text(desc)
                },
                confirmButton = {
                    TextButton(
                        onClick = onConfirm
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text("Batal")
                    }

                }
            )
        }

    }
}
